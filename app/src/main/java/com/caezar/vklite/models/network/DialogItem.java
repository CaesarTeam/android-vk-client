package com.caezar.vklite.models.network;

/**
 * Created by seva on 11.04.18 in 15:08.
 */

@SuppressWarnings({"unused"})
public class DialogItem {
    private int unread;
    private int in_read;
    private int out_read;
    private boolean online;
    private int peerId;
    private DialogMessage message;

    public int getPeerId() {
        return peerId;
    }

    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public DialogItem() {
        message = new DialogMessage();
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public int getIn_read() {
        return in_read;
    }

    public void setIn_read(int in_read) {
        this.in_read = in_read;
    }

    public int getOut_read() {
        return out_read;
    }

    public void setOut_read(int out_read) {
        this.out_read = out_read;
    }

    public DialogMessage getMessage() {
        return message;
    }

    public void setMessage(DialogMessage message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "DialogItem{" +
                "unread=" + unread +
                ", in_read=" + in_read +
                ", out_read=" + out_read +
                ", message_text=" + message +
                '}';
    }
}
