package com.caezar.vklite.network.models;

/**
 * Created by seva on 04.04.18 in 0:34.
 */
public class SendMessageRequest {
    private int peer_id;
    private String message;

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

    public SendMessageRequest() {
    }

    @Override
    public String toString() {
        return "&peer_id=" + peer_id +
                "&message=" + message;
    }
}
