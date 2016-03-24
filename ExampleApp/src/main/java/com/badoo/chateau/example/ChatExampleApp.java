package com.badoo.chateau.example;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.badoo.barf.data.repo.Repositories;
import com.badoo.barf.data.repo.annotations.HandlesUtil;
import com.badoo.chateau.Broadcaster;
import com.badoo.chateau.core.repos.conversations.ConversationRepository;
import com.badoo.chateau.core.repos.istyping.IsTypingRepository;
import com.badoo.chateau.core.repos.messages.MessageRepository;
import com.badoo.chateau.core.repos.users.UserRepository;
import com.badoo.chateau.data.repos.session.SessionRepository;
import com.badoo.chateau.example.ui.util.BackgroundListenerRegistrar;
import com.badoo.chateau.example.data.repos.conversations.ParseConversationDataSource;
import com.badoo.chateau.example.data.repos.istyping.ParseIsTypingDataSource;
import com.badoo.chateau.example.data.repos.messages.ImageUploadService;
import com.badoo.chateau.example.data.repos.messages.ParseMessageDataSource;
import com.badoo.chateau.example.data.repos.session.ParseSessionDataSource;
import com.badoo.chateau.example.data.repos.user.ParseUserDataSource;
import com.badoo.chateau.example.data.util.ParseHelper;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.example.ui.chat.ChatActivity;
import com.badoo.chateau.example.ui.conversations.create.namegroup.NameGroupActivity;
import com.badoo.chateau.example.ui.conversations.create.selectusers.SelectUserActivity;
import com.badoo.chateau.example.ui.conversations.list.ConversationListActivity;
import com.badoo.chateau.example.ui.session.login.LoginActivity;
import com.badoo.chateau.example.ui.session.register.RegisterActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import net.hockeyapp.android.CrashManager;

public class ChatExampleApp extends Application {

    private static final int SOCKET_PORT = 9000;
    private static final String HOCKEY_APP_ID = "4f7532de86894fe1a8caf9b388dd888d"; //TODO Move to build param

    private final BackgroundListenerRegistrar mBackgroundListenerRegistrar = new BackgroundListenerRegistrar();

    private SocketNotificationClient mSocketClient;

    @Override
    public void onCreate() {
        super.onCreate();
        final Broadcaster broadcaster = new Broadcaster(getApplicationContext());

        CrashManager.register(this, HOCKEY_APP_ID);
        // Register for socket notifications
        mSocketClient = new SocketNotificationClient(broadcaster, BuildConfig.SOCKET_NOTIFICATION_ENDPOINT, SOCKET_PORT);

        mBackgroundListenerRegistrar.register(this);
        mBackgroundListenerRegistrar.registerBackgroundListener(new BackgroundListenerRegistrar.BackgroundListener() {
            @Override
            public void movedToForeground() {
                mSocketClient.start();
            }

            @Override
            public void movedToBackground() {
                mSocketClient.stop();
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(new Broadcaster.UserSignInStateChangedReceiver() {

            @Override
            public void onUserSignedIn() {
                mSocketClient.restart();
            }

            @Override
            public void onUserSignedOut() {
                mSocketClient.restart();
            }
        }, Broadcaster.getUserSignInStateChangedFilter());

        // Parse
        Parse.initialize(this, BuildConfig.PARSE_APP_ID, BuildConfig.PARSE_CLIENT_KEY);
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            installation.put("user", user);
        }
        installation.saveInBackground();

        // Fresco
        final ImagePipelineConfig config = ImagePipelineConfig.newBuilder(getApplicationContext())
                .setDownsampleEnabled(true)
                .setWebpSupportEnabled(true)
                .build();
        Fresco.initialize(getApplicationContext(), config);

        // Repos
        Repositories.registerRepo(SessionRepository.KEY, new SessionRepository(new ParseSessionDataSource(broadcaster, ParseHelper.INSTANCE)));

        Repositories.registerRepo(ConversationRepository.KEY, createConversationsRepo());
        Repositories.registerRepo(UserRepository.KEY, createUserRepository());
        Repositories.registerRepo(MessageRepository.KEY, createMessageRepo());

        final IsTypingRepository typingRepository = new IsTypingRepository();
        HandlesUtil.registerHandlersFromAnnotations(typingRepository, new ParseIsTypingDataSource(getApplicationContext()));
        Repositories.registerRepo(IsTypingRepository.KEY, typingRepository);

        registerInjections();
    }

    private ConversationRepository createConversationsRepo() {
        final ConversationRepository conversationRepository = new ConversationRepository();
        final ParseConversationDataSource parseConversationDataSource = new ParseConversationDataSource(ParseHelper.INSTANCE);

        HandlesUtil.registerHandlersFromAnnotations(conversationRepository, parseConversationDataSource);
        Broadcaster.ConversationUpdatedReceiver pullLatestMessagesReceiver = new Broadcaster.ConversationUpdatedReceiver() {
            @Override
            public void onConversationUpdated(@NonNull String chatId, long timestamp) {
                parseConversationDataSource.reloadConversation(chatId);
            }

            @Override
            public void onImageUploaded(@NonNull String chatId, @NonNull String messageId) {
                // Not used
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(pullLatestMessagesReceiver, Broadcaster.getConversationUpdatedFilter());
        return conversationRepository;
    }

    @NonNull
    private UserRepository createUserRepository() {
        final UserRepository userRepository = new UserRepository();
        HandlesUtil.registerHandlersFromAnnotations(userRepository, new ParseUserDataSource(ParseHelper.INSTANCE));
        return userRepository;
    }

    private MessageRepository createMessageRepo() {
        final MessageRepository messageRepository = new MessageRepository();
        final ParseMessageDataSource.ImageUploader imageUploader = (localId, uri) -> startService(ImageUploadService.createIntent(getApplicationContext(), localId, uri));
        final ParseMessageDataSource parseMessageDataSource = new ParseMessageDataSource(imageUploader, ParseHelper.INSTANCE);

        HandlesUtil.registerHandlersFromAnnotations(messageRepository, parseMessageDataSource);
        Broadcaster.ConversationUpdatedReceiver pullLatestMessagesReceiver = new Broadcaster.ConversationUpdatedReceiver() {

            @Override
            public void onConversationUpdated(@NonNull String chatId, long timestamp) {
                parseMessageDataSource.pullLatestMessages(chatId, timestamp);

            }

            @Override
            public void onImageUploaded(@NonNull String chatId, @NonNull String messageId) {
                parseMessageDataSource.updateMessage(chatId, messageId);
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(pullLatestMessagesReceiver, Broadcaster.getConversationUpdatedFilter());
        return messageRepository;
    }

    protected void registerInjections() {
        Injector.register(RegisterActivity.class, new RegisterActivity.DefaultConfiguration());
        Injector.register(LoginActivity.class, new LoginActivity.DefaultConfiguration());

        Injector.register(ConversationListActivity.class, new ConversationListActivity.DefaultConfiguration());
        Injector.register(NameGroupActivity.class, new NameGroupActivity.DefaultConfiguration());
        Injector.register(SelectUserActivity.class, new SelectUserActivity.DefaultConfiguration());

        Injector.register(ChatActivity.class, new ChatActivity.DefaultConfiguration());
    }
}
