package com.badoo.chateau.example.ui.chat.messages;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.badoo.barf.mvp.BaseView;
import com.badoo.chateau.example.R;
import com.badoo.chateau.data.models.BaseMessage;
import com.badoo.chateau.data.models.payloads.ImagePayload;
import com.badoo.chateau.data.models.payloads.TextPayload;
import com.badoo.chateau.data.models.payloads.TimestampPayload;
import com.badoo.chateau.example.ui.chat.messages.viewholders.ImageMessageViewHolder;
import com.badoo.chateau.example.ui.chat.messages.viewholders.TextMessageViewHolder;
import com.badoo.chateau.example.ui.chat.messages.viewholders.TimestampViewHolder;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.chat.messages.MessageListPresenter;
import com.badoo.chateau.example.ui.util.BaseMessageViewHolder;
import com.badoo.chateau.example.ui.util.recycle.LoadingAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MessageListViewImpl extends BaseView<MessageListPresenter> implements MessageListPresenter.MessageListView {

    public static final long HIDE_IS_TYPING_INDICATOR = TimeUnit.SECONDS.toMillis(5);
    private final RecyclerView mChatList;
    private final LinearLayoutManager mLayoutManager;

    private final MessageListAdapter mMessageListAdapter = new MessageListAdapter();
    private final LoadingAdapter<BaseMessageViewHolder> mLoadingAdapter = new LoadingAdapter<>(mMessageListAdapter, Integer.MIN_VALUE);
    private final ActionBar mSupportActionBar;
    private final Handler mTypingHandler = new Handler(Looper.getMainLooper());

    public MessageListViewImpl(@NonNull ViewFinder finder, @NonNull ActionBar supportActionBar) {
        mSupportActionBar = supportActionBar;
        mChatList = finder.findViewById(R.id.chat_list);
        mLayoutManager = new LinearLayoutManager(mChatList.getContext());
        mLayoutManager.setStackFromEnd(true);
        mChatList.setLayoutManager(mLayoutManager);
        mLoadingAdapter.setLoading(false);
        mChatList.setAdapter(mLoadingAdapter);


        mMessageListAdapter.registerMessageViewHolderFactory(TextPayload.class, parent -> {
            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_text_message, parent, false);
            return new TextMessageViewHolder(v);
        }, new BaseItemClickListener(mChatList, mMessageListAdapter));

        mMessageListAdapter.registerMessageViewHolderFactory(ImagePayload.class, parent -> {
            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_image_message, parent, false);
            return new ImageMessageViewHolder(v);
        }, new BaseItemClickListener(mChatList, mMessageListAdapter) {
            @Override
            public void onClick(@NonNull BaseMessage message) {
                // TODO: Don't allow click when image is loading?
                getPresenter().onImageClicked(Uri.parse(((ImagePayload) message.getPayload()).getImageUrl()));
            }
        });

        mMessageListAdapter.registerMessageViewHolderFactory(TimestampPayload.class, parent -> {
            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_day, parent, false);
            return new TimestampViewHolder(v);
        });

        mMessageListAdapter.registerPreProcessor(new TimeStampPreProcessor());

        mChatList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                notifyIfReachingTopOfList();
            }
        });
    }

    @Override
    public void showGenericError() {
        Snackbar.make(mChatList, R.string.error_generic, Snackbar.LENGTH_INDEFINITE).show();
    }

    private void notifyIfReachingTopOfList() {
        if (mLayoutManager.findFirstCompletelyVisibleItemPosition() < 1) {
            getPresenter().onMoreMessagesRequired();
        }
    }

    @Override
    public void setTitle(@NonNull String title) {
        mSupportActionBar.setTitle(title);
    }

    @Override
    public void showOtherUserTyping() {
        mSupportActionBar.setSubtitle(R.string.is_typing);
        mTypingHandler.removeCallbacksAndMessages(null);
        mTypingHandler.postDelayed(this::clearIsTyping, HIDE_IS_TYPING_INDICATOR);
    }

    @Override
    public void showLoadingMoreMessages(boolean show) {
        mLoadingAdapter.setLoading(show);
    }

    @Override
    public void showMessage(@NonNull BaseMessage message) {
        if (!message.isFromMe()) {
            clearIsTyping();
        }
        final boolean scrollToEndAfterUpdate = isLastItemDisplayed();
        final boolean tailUpdated = mMessageListAdapter.addMessage(message);
        if ((scrollToEndAfterUpdate || message.isFromMe()) && tailUpdated) {
            mChatList.smoothScrollToPosition(mMessageListAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void showMessages(@NonNull List<BaseMessage> messages) {
        final boolean scrollToEndAfterUpdate = isLastItemDisplayed();
        final boolean tailUpdated = mMessageListAdapter.addMessages(messages);
        if (scrollToEndAfterUpdate && tailUpdated) {
            mChatList.scrollToPosition(mMessageListAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void replaceMessage(@NonNull BaseMessage message) {
        mMessageListAdapter.replaceMessage(message);
    }

    /**
     * returns <code>true</code> if the last item in the list is been displayed.
     */
    private boolean isLastItemDisplayed() {
        return mLoadingAdapter.getItemCount() == 0 || mLayoutManager.findLastVisibleItemPosition() == (mLoadingAdapter.getItemCount() - 1);
    }

    private void clearIsTyping() {
        mSupportActionBar.setSubtitle("");
    }
}
