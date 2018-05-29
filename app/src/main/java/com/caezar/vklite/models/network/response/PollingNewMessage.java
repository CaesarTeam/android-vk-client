package com.caezar.vklite.models.network.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by seva on 29.05.18 in 15:46.
 */

public class PollingNewMessage implements Parcelable {
    private int messageId;
    private int flags;
    private int peerId;
    private int timestamp;
    private String message;
    private int fromId;

    public int getMessageId() {
        return messageId;
    }

    public int getFlags() {
        return flags;
    }

    public int getPeerId() {
        return peerId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public int getFromId() {
        return fromId;
    }

    public PollingNewMessage(int messageId, int flags, int peerId, int timestamp, String message, int fromId) {
        this.messageId = messageId;
        this.flags = flags;
        this.peerId = peerId;
        this.timestamp = timestamp;
        this.message = message;
        this.fromId = fromId;
    }

    public PollingNewMessage() {

    }

    @Override
    public String toString() {
        return "PollingNewMessage{" +
                "messageId=" + messageId +
                ", flags=" + flags +
                ", peerId=" + peerId +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", fromId=" + fromId +
                '}';
    }

    protected PollingNewMessage(Parcel in) {
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

    public static final Creator<PollingNewMessage> CREATOR = new Creator<PollingNewMessage>() {
        @Override
        public PollingNewMessage createFromParcel(Parcel in) {
            return new PollingNewMessage(in);
        }

        @Override
        public PollingNewMessage[] newArray(int size) {
            return new PollingNewMessage[size];
        }
    };
}
