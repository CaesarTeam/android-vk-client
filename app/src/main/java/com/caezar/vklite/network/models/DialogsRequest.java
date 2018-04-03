package com.caezar.vklite.network.models;

/**
 * Created by seva on 25.03.18 in 18:15.
 */
// todo: annotation unsigned, max, min

public class DialogsRequest {
    private int offset;
    private int count = 20;
    private long start_message_id;
    private int preview_length;
    private boolean unread;
    private boolean important;
    private boolean unanswered;

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

    public long getStart_message_id() {
        return start_message_id;
    }

    public void setStart_message_id(long start_message_id) {
        this.start_message_id = start_message_id;
    }

    public int getPreview_length() {
        return preview_length;
    }

    public void setPreview_length(int preview_length) {
        this.preview_length = preview_length;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public boolean isUnanswered() {
        return unanswered;
    }

    public void setUnanswered(boolean unanswered) {
        this.unanswered = unanswered;
    }

    public DialogsRequest() {

    }

    @Override
    public String toString() {
        return  "offset=" + offset +
                "&count=" + count +
                "&start_message_id=" + start_message_id +
                "&preview_length=" + preview_length +
                "&unread=" + (unread ? 1 : 0) +
                "&important=" + (important ? 1 : 0) +
                "&unanswered=" + (unanswered ? 1 : 0);
    }
}