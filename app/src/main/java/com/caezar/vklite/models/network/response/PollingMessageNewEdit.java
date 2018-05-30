package com.caezar.vklite.models.network.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by seva on 29.05.18 in 15:46.
 */

public class PollingMessageNewEdit extends PollingMessageBase implements Parcelable {
    private int timestamp;
    private String message;
    private int fromId;

    public int getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public int getFromId() {
        return fromId;
    }

    public PollingMessageNewEdit(int messageId, int flags, int peerId, int timestamp, String message, int fromId) {
        super(messageId, flags, peerId);
        this.timestamp = timestamp;
        this.message = message;
        this.fromId = fromId;
    }

    public PollingMessageNewEdit() {
        super();
    }

    @Override
    public String toString() {
        return "PollingMessageNewEdit{" +
                "messageId=" + messageId +
                ", flags=" + flags +
                ", peerId=" + peerId +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", fromId=" + fromId +
                '}';
    }

    protected PollingMessageNewEdit(Parcel in) {
        messageId = in.readInt();
        flags = in.readInt();
        peerId = in.readInt();
        timestamp = in.readInt();
        message = in.readString();
        fromId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(messageId);
        dest.writeInt(flags);
        dest.writeInt(peerId);
        dest.writeInt(timestamp);
        dest.writeString(message);
        dest.writeInt(fromId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PollingMessageNewEdit> CREATOR = new Creator<PollingMessageNewEdit>() {
        @Override
        public PollingMessageNewEdit createFromParcel(Parcel in) {
            return new PollingMessageNewEdit(in);
        }

        @Override
        public PollingMessageNewEdit[] newArray(int size) {
            return new PollingMessageNewEdit[size];
        }
    };
}
