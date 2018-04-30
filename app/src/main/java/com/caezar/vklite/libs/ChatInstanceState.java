package com.caezar.vklite.libs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.caezar.vklite.models.network.DialogMessage;

import java.util.List;

/**
 * Created by seva on 30.04.18 in 13:33.
 */

public class ChatInstanceState {
    @NonNull
    private final static ChatInstanceState INSTANCE = new ChatInstanceState();

    @Nullable
    private List<DialogMessage> messages = null;
    @Nullable
    private SparseArray<String> photoUsers = null;

    private ChatInstanceState() {
    }

    public void resetChat() {
        messages = null;
        photoUsers = null;
    }

    public static ChatInstanceState getInstance() {
        return INSTANCE;
    }

    @Nullable
    public List<DialogMessage> getMessages() {
        return messages;
    }

    public void setMessages(@Nullable List<DialogMessage> messages) {
        this.messages = messages;
    }

    @Nullable
    public SparseArray<String> getPhotoUsers() {
        return photoUsers;
    }

    public void setPhotoUsers(@Nullable SparseArray<String> photoUsers) {
        this.photoUsers = photoUsers;
    }
}
