package com.caezar.vklite.models.network.request;

/**
 * Created by seva on 25.03.18 in 18:16.
 */

@SuppressWarnings({"unused"})
public class ChatRequest {
    private int offset;
    private int count = 80;
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
        StringBuilder query = new StringBuilder(
                "offset=" + offset +
                "&count=" + count +
                "&peer_id=" + peer_id +
                "&rev=" + rev
        );


        if (start_message_id != 0) {
            query.append("&start_message_id=").append(start_message_id);
        }

        return query.toString();
    }
}
