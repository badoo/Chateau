package com.badoo.chateau.example.ui.chat.messages;

import android.support.annotation.NonNull;

import com.badoo.chateau.example.data.model.ExampleMessage;

import java.util.List;

/**
 * Preprocessor that can add or remove items from the list of messages before it's displayed
 */
public interface ItemPreProcessor {

    List<ExampleMessage> doProcess(@NonNull List<ExampleMessage> input);
}
