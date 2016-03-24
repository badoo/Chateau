package com.badoo.chateau.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.badoo.chateau.example.ui.conversations.list.ConversationListActivity;
import com.badoo.chateau.example.ui.session.login.LoginActivity;
import com.parse.ParseUser;

public class LandingActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null) {
            openConversationsList();
        }
        else {
            openLogin();
        }
        finish();
    }

    private void openConversationsList() {
        final Intent intent = new Intent(this, ConversationListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void openLogin() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
