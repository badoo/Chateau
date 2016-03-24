package com.badoo.chateau.example.ui.widgets;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

class InputActionsMenu implements Menu {

    private final Context mContext;
    private final List<InputActionsMenuItem> mItems = new ArrayList<>();

    public InputActionsMenu(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public MenuItem add(CharSequence title) {
        InputActionsMenuItem item = new InputActionsMenuItem();
        item.setTitle(title);
        mItems.add(item);
        return item;
    }

    @Override
    public MenuItem add(int titleRes) {
        InputActionsMenuItem item = new InputActionsMenuItem();
        item.setTitle(titleRes);
        mItems.add(item);
        return item;
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
        InputActionsMenuItem item = new InputActionsMenuItem(itemId);
        item.setTitle(title);
        mItems.add(item);
        return item;
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, int titleRes) {
        InputActionsMenuItem item = new InputActionsMenuItem(itemId);
        item.setTitle(titleRes);
        mItems.add(item);
        return item;
    }

    @Override
    public SubMenu addSubMenu(CharSequence title) {
        // Not supported
        return null;
    }

    @Override
    public SubMenu addSubMenu(int titleRes) {
        // Not supported
        return null;
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
        // Not supported
        return null;
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
        // Not supported
        return null;
    }

    @Override
    public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
        // Not supported
        return 0;
    }

    @Override
    public void removeItem(int id) {
        // Not supported
    }

    @Override
    public void removeGroup(int groupId) {
        // Not supported
    }

    @Override
    public void clear() {
        // Not supported
    }

    @Override
    public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {
        // Not supported
    }

    @Override
    public void setGroupVisible(int group, boolean visible) {
        // Not supported
    }

    @Override
    public void setGroupEnabled(int group, boolean enabled) {
        // Not supported
    }

    @Override
    public boolean hasVisibleItems() {
        return true;
    }

    @Override
    public MenuItem findItem(int id) {
        // Not supported
        return null;
    }

    @Override
    public int size() {
        return mItems.size();
    }

    @Override
    public MenuItem getItem(int index) {
        return mItems.get(index);
    }

    @Override
    public void close() {
        // Not supported
    }

    @Override
    public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
        // Not supported
        return false;
    }

    @Override
    public boolean isShortcutKey(int keyCode, KeyEvent event) {
        // Not supported
        return false;
    }

    @Override
    public boolean performIdentifierAction(int id, int flags) {
        // Not supported
        return false;
    }

    @Override
    public void setQwertyMode(boolean isQwerty) {
        // Not supported
    }

    class InputActionsMenuItem implements MenuItem {

        private int mItemId;
        private CharSequence mTitle;
        private Drawable mIcon;

        public InputActionsMenuItem() {
        }

        public InputActionsMenuItem(int itemId) {
            mItemId = itemId;
        }

        @Override
        public int getItemId() {
            return mItemId;
        }

        @Override
        public int getGroupId() {
            return 0;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public MenuItem setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        @Override
        public MenuItem setTitle(int title) {
            mTitle = mContext.getString(title);
            return this;
        }

        @Override
        public CharSequence getTitle() {
            return mTitle;
        }

        @Override
        public MenuItem setTitleCondensed(CharSequence title) {
            // Not Supported
            return this;
        }

        @Override
        public CharSequence getTitleCondensed() {
            // Not supported
            return null;
        }

        @Override
        public MenuItem setIcon(Drawable icon) {
            mIcon = icon;
            return this;
        }

        @Override
        public MenuItem setIcon(int iconRes) {
            //noinspection deprecation
            mIcon = mContext.getResources().getDrawable(iconRes);
            return this;
        }

        @Override
        public Drawable getIcon() {
            return mIcon;
        }

        @Override
        public MenuItem setIntent(Intent intent) {
            // Not supported
            return this;
        }

        @Override
        public Intent getIntent() {
            // Not supported
            return null;
        }

        @Override
        public MenuItem setShortcut(char numericChar, char alphaChar) {
            // Not supported
            return this;
        }

        @Override
        public MenuItem setNumericShortcut(char numericChar) {
            // Not supported
            return this;
        }

        @Override
        public char getNumericShortcut() {
            return 0;
        }

        @Override
        public MenuItem setAlphabeticShortcut(char alphaChar) {
            // Not supported
            return this;
        }

        @Override
        public char getAlphabeticShortcut() {
            // Not supported
            return 0;
        }

        @Override
        public MenuItem setCheckable(boolean checkable) {
            // Not supported
            return this;
        }

        @Override
        public boolean isCheckable() {
            // Not supported
            return false;
        }

        @Override
        public MenuItem setChecked(boolean checked) {
            // Not supported
            return this;
        }

        @Override
        public boolean isChecked() {
            // Not supported
            return false;
        }

        @Override
        public MenuItem setVisible(boolean visible) {
            // Not supported
            return this;
        }

        @Override
        public boolean isVisible() {
            // Not supported
            return true;
        }

        @Override
        public MenuItem setEnabled(boolean enabled) {
            // Not supported
            return this;
        }

        @Override
        public boolean isEnabled() {
            // Not supported
            return true;
        }

        @Override
        public boolean hasSubMenu() {
            return false;
        }

        @Override
        public SubMenu getSubMenu() {
            // Not supported
            return null;
        }

        @Override
        public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
            // Not supported
            return this;
        }

        @Override
        public ContextMenu.ContextMenuInfo getMenuInfo() {
            // Not supported
            return null;
        }

        @Override
        public void setShowAsAction(int actionEnum) {
            // Not supported
        }

        @Override
        public MenuItem setShowAsActionFlags(int actionEnum) {
            // Not supported
            return this;
        }

        @Override
        public MenuItem setActionView(View view) {
            // Not supported
            return this;
        }

        @Override
        public MenuItem setActionView(int resId) {
            // Not supported
            return this;
        }

        @Override
        public View getActionView() {
            // Not supported
            return null;
        }

        @Override
        public MenuItem setActionProvider(ActionProvider actionProvider) {
            // Not supported
            return this;
        }

        @Override
        public ActionProvider getActionProvider() {
            // Not supported
            return null;
        }

        @Override
        public boolean expandActionView() {
            // Not supported
            return false;
        }

        @Override
        public boolean collapseActionView() {
            // Not supported
            return false;
        }

        @Override
        public boolean isActionViewExpanded() {
            // Not supported
            return false;
        }

        @Override
        public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
            // Not supported
            return this;
        }
    }

}