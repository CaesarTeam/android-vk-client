package com.caezar.vklite.models.network;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by seva on 11.04.18 in 15:08.
 */

@SuppressWarnings({"unused"})
public class DialogItem implements Parcelable {
    private int  unread;
    private int  in_read;
    private int  out_read;

    private DialogMessage message;

    public DialogItem() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}