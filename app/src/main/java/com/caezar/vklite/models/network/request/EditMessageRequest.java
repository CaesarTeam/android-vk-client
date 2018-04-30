package com.caezar.vklite.models.network.request;

/**
 * Created by seva on 30.04.18 in 16:00.
 */

public class EditMessageRequest {
    int peer_id;
    String message;
    int message_id;

    public EditMessageRequest() {
    }

    public int getPeer_id() {
        return peer_id;
    }

    public void setPeer_id(int peer_id) {
        this.peer_id = peer_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    @Override
    public String toString() {
        return "peer_id=" + peer_id +
                "&message=" + message +
                "&message_id=" + message_id;
    }
}