package com.caezar.vklite.models.network.request;

/**
 * Created by seva on 12.04.18 in 21:26.
 */

@SuppressWarnings({"unused"})
public class UsersChatRequest {
    private int chat_id;

    public int getChat_id() {
        return chat_id;
    }

    public void setChat_id(int chat_id) {
        this.chat_id = chat_id;
    }

    public UsersChatRequest() {

    }

    @Override
    public String toString() {
        return "chat_id=" + chat_id;
    }
}
