package com.badoo.chateau.example.ui.chat.messages;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.badoo.barf.mvp.MvpView;
import com.badoo.barf.mvp.PresenterFactory;
import com.badoo.chateau.data.models.payloads.ImagePayload;
import com.badoo.chateau.data.models.payloads.TextPayload;
import com.badoo.chateau.data.models.payloads.TimestampPayload;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.ui.chat.messages.viewholders.ImageMessageViewHolder;
import com.badoo.chateau.example.ui.chat.messages.viewholders.TextMessageViewHolder;
import com.badoo.chateau.example.ui.chat.messages.viewholders.TimestampViewHolder;
import com.badoo.chateau.example.ui.util.MessageViewHolder;
import com.badoo.chateau.example.ui.util.recycle.LoadingAdapter;
import com.badoo.chateau.extras.ViewFinder;

import java.util.List;

public class ExampleMessageListViewImpl implements ExampleMessageListView, MvpView {

    private static final String TAG = ExampleMessageListViewImpl.class.getSimpleName();
    private static final boolean DEBUG = true;

    private final RecyclerView mChatList;
    private final LinearLayoutManager mLayoutManager;

    private final MessageListAdapter mMessageListAdapter = new MessageListAdapter();
    private final LoadingAdapter<MessageViewHolder> mLoadingAdapter = new LoadingAdapter<>(mMessageListAdapter, Integer.MIN_VALUE);
    private ExampleMessageListPresenter mPresenter;
    private boolean mHasRequestedMoreMessages;

    public ExampleMessageListViewImpl(@NonNull ViewFinder finder,
                                      @NonNull PresenterFactory<ExampleMessageListView, ExampleMessageListPresenter> presenterFactory) {
        mPresenter = presenterFactory.init(this);
        mChatList = finder.findViewById(R.id.chat_list);
        mLayoutManager = new LinearLayoutManager(mChatList.getContext());
        mLayoutManager.setStackFromEnd(true);
        mChatList.setLayoutManager(mLayoutManager);
        mLoadingAdapter.setLoading(false);
        mLoadingAdapter.setHasStableIds(true);
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
            public void onClick(@NonNull ExampleMessage message) {
                // TODO: Don't allow click when image is loading?
                mPresenter.onImageClicked(Uri.parse(((ImagePayload) message.getPayload()).getImageUrl()));
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
    public void showError(boolean fatal, Throwable throwable) {
        if (fatal) {
            Snackbar.make(mChatList, R.string.error_generic, Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    private void notifyIfReachingTopOfList() {
        if (!mHasRequestedMoreMessages && mLayoutManager.findFirstCompletelyVisibleItemPosition() < 10) {
            mPresenter.onMoreMessagesRequired();
            mHasRequestedMoreMessages = true;
        }
    }

    @Override
    public void showLoadingMoreMessages(boolean show) {
        mLoadingAdapter.setLoading(show);
    }

    @Override
    public void showMessages(@NonNull List<ExampleMessage> messages) {
        mMessageListAdapter.setMessages(messages);
        if (mMessageListAdapter.getItemCount() == 0) {
            mChatList.scrollToPosition(mMessageListAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void showNewerMessages(@NonNull List<ExampleMessage> messages) {
        mMessageListAdapter.addNewMessages(messages);
        mChatList.scrollToPosition(mMessageListAdapter.getItemCount() - 1);
    }

    @Override
    public void showOlderMessages(@NonNull List<ExampleMessage> messages) {
        mMessageListAdapter.addOldMessages(messages);
    }

    @Override
    public void replaceMessage(@NonNull ExampleMessage oldMessage, @NonNull ExampleMessage newMessage) {
        mMessageListAdapter.replaceMessage(oldMessage, newMessage);
    }

//    @Override
//    public void showMessages(@NonNull List<ExampleMessage> messages) {
//        // When items are added we either want to scroll to the bottom, or if a block of messages were added to the top then we want
//        // to maintain the position (the RecyclerView does not handle this properly even with stable ids enabled)
//        final boolean addedToTop = !messages.isEmpty() && !messages.get(0).equals(mMessageListAdapter.getOldestMessage());
//        final boolean scrollToEndAfterUpdate = isLastItemDisplayed();
//        final int lastVisiblePosition = mLoadingAdapter.getAdjustedPosition(mLayoutManager.findLastCompletelyVisibleItemPosition());
//        final ExampleMessage lastVisible = lastVisiblePosition != -1 && mMessageListAdapter.getItemCount() > 0 ? mMessageListAdapter.getMessage(lastVisiblePosition) : null;
//        mMessageListAdapter.setMessages(messages);
//        if (scrollToEndAfterUpdate) {
//            mChatList.scrollToPosition(mMessageListAdapter.getItemCount() - 1);
//        }
//        else if (addedToTop) {
//            int index = mMessageListAdapter.getIndexOfMessage(lastVisible);
//            if (index != -1) {
//                mChatList.scrollToPosition(index);
//            }
//        }
//        mHasRequestedMoreMessages = false;
//        if (DEBUG) {
//            Log.d(TAG, "Showing messages, scrollToBottom:" + scrollToEndAfterUpdate + ", maintainPosition:" + addedToTop);
//        }
//    }

    /**
     * returns <code>true</code> if the last item in the list is been displayed.
     */
    private boolean isLastItemDisplayed() {
        return mLoadingAdapter.getItemCount() == 0 || mLayoutManager.findLastVisibleItemPosition() == (mLoadingAdapter.getItemCount() - 1);
    }

}
