package com.caezar.vklite.network.models;

/**
 * Created by seva on 25.03.18 in 18:16.
 */

public class ChatRequest {
    private int offset;
    private int count = 20;
    private String user_id;
    private int peer_id;
    private int start_message_id;
    private int rev;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getPeer_id() {
        return peer_id;
    }

    public void setPeer_id(int peer_id) {
        this.peer_id = peer_id;
    }

    public int getStart_message_id() {
        return start_message_id;
    }

    public void setStart_message_id(int start_message_id) {
        this.start_message_id = start_message_id;
    }

    public int getRev() {
        return rev;
    }

    public void setRev(int rev) {
        this.rev = rev;
    }

    public ChatRequest() {

    }

    @Override
    public String toString() {
        return "offset=" + offset +
                "&count=" + count +
//                "&user_id=" + user_id +
                "&peer_id=" + peer_id +
//                "&start_message_id=" + start_message_id +
                "&rev=" + rev;
    }
}
