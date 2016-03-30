package com.badoo.chateau.example.ui.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.badoo.chateau.example.R;

/**
 * View providing a text input field, a row of icons for actions (e.g Add image, take picture, etc) and a Send button.
 */
public class ChatTextInputView extends FrameLayout {

    @Nullable
    private OnTypingListener mOnTypingListener;
    @Nullable
    private OnClickListener mOnSendClickListener;
    private EditText mEditText;
    private FloatingActionButton mSendEnabled; // Got two of these to workaround tinting issues on Android 4.4
    private FloatingActionButton mSendDisabled;
    private ViewGroup mActionsContainer;
    private MenuItem.OnMenuItemClickListener mOnMenuItemClickedListener;
    private InputActionsMenu mMenu;
    private long mLastOnTypingNotification; // Keep track of last notification time for throttling

    public ChatTextInputView(Context context) {
        super(context);
    }

    public ChatTextInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(attrs);
    }

    public ChatTextInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttributes(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChatTextInputView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parseAttributes(attrs);
    }

    private void parseAttributes(@NonNull AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
            attrs,
            R.styleable.ChatTextInputView,
            0, 0);

        try {
            int menuRes = a.getResourceId(R.styleable.ChatTextInputView_actionMenu, -1);
            if (menuRes != -1) {
                setActions(menuRes);
            }
        } finally {
            a.recycle();
        }
    }

    /**
     * Sets the listener to be invoked when the user taps the Send button.
     */
    public void setOnSendClickListener(@Nullable OnClickListener listener) {
        mOnSendClickListener = listener;
    }

    /**
     * Sets the listener to be invoked when the user is entering text in the text field.
     */
    public void setOnTypingListener(@Nullable OnTypingListener listener) {
        mOnTypingListener = listener;
    }

    /**
     * Sets the listener that will be invoked if an action item is clicked.
     */
    public void setOnActionItemClickedListener(@Nullable MenuItem.OnMenuItemClickListener listener) {
        mOnMenuItemClickedListener = listener;
    }

    /**
     * Sets the actions to be displayed below the text field. Only the icon and id is used from each menu item.
     */
    public void setActions(@MenuRes int menuRes) {
        mMenu = new InputActionsMenu(getContext());
        new MenuInflater(getContext()).inflate(menuRes, mMenu);
        populateMenuIfReady();
    }

    /**
     * Returns the text currently entered into the text field.
     *
     * @return the text in the text field
     */
    @NonNull
    public String getText() {
        return mEditText.getText().toString();
    }

    /**
     * Clears the text in the text field.
     */
    public void clearText() {
        // On the LG G4 getText().clear() does not work so doing this instead. Thanks LG!
        mEditText.setText("", TextView.BufferType.EDITABLE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.view_chat_text_input, this);
        mActionsContainer = (ViewGroup) findViewById(R.id.chatTextInput_actionsContainer);
        mEditText = (EditText) findViewById(R.id.chatTextInput_editText);
        mEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) {
                    return;
                }
                long elapsedTime = SystemClock.elapsedRealtime() - mLastOnTypingNotification;
                if (mOnTypingListener != null && s.length() > 0 && elapsedTime > 2000) {
                    mLastOnTypingNotification = SystemClock.elapsedRealtime();
                    mOnTypingListener.onTyping();
                }
                updateSendButtonState(s.length() > 0);
            }
        });
        mSendEnabled = (FloatingActionButton) findViewById(R.id.chatTextInput_sendEnabled);
        mSendEnabled.setEnabled(true);
        mSendEnabled.setVisibility(View.GONE);
        mSendEnabled.setOnClickListener(v -> {
            if (mOnSendClickListener != null) {
                mOnSendClickListener.onClick(v);
            }
        });
        mSendDisabled = (FloatingActionButton) findViewById(R.id.chatTextInput_sendDisabled);
        mSendDisabled.setEnabled(false);
        mSendDisabled.setVisibility(View.VISIBLE);
        populateMenuIfReady();
    }

    private void updateSendButtonState(boolean enable) {
        if ((enable && mSendEnabled.getVisibility() == VISIBLE) || (!enable && mSendDisabled.getVisibility() == VISIBLE)) {
            // Already in correct state
            return;
        }
        if (enable) {
            mSendDisabled.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    mSendEnabled.show();
                }
            });
        }
        else {
            mSendEnabled.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    mSendDisabled.show();
                }
            });
        }
    }

    private final OnClickListener mOnActionItemViewClicked = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnMenuItemClickedListener != null) {
                mOnMenuItemClickedListener.onMenuItemClick((MenuItem) v.getTag());
            }
        }
    };

    private void populateMenuIfReady() {
        if (mMenu == null || mActionsContainer == null) {
            return;
        }
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        mActionsContainer.removeAllViews();
        for (int i = 0; i < mMenu.size(); i++) {
            MenuItem item = mMenu.getItem(i);
            final View itemView = inflater.inflate(R.layout.item_input_action, mActionsContainer, false);
            final ImageView itemIcon = (ImageView) itemView.findViewById(R.id.inputAction_image);
            itemView.setTag(item);
            itemView.setId(item.getItemId());
            itemIcon.setImageDrawable(item.getIcon());
            itemView.setOnClickListener(mOnActionItemViewClicked);
            mActionsContainer.addView(itemView);
        }
    }

    public interface OnTypingListener {

        void onTyping();

    }

}
