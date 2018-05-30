package com.caezar.vklite.models.network.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by seva on 30.05.18 in 11:00.
 */

public class PollingMessageBase implements Parcelable {
    protected int messageId;
    protected int flags;
    protected int peerId;

    public int getMessageId() {
        return messageId;
    }

    public int getFlags() {
        return flags;
    }

    public int getPeerId() {
        return peerId;
    }

    public PollingMessageBase(int messageId, int flags, int peerId) {
        this.messageId = messageId;
        this.flags = flags;
        this.peerId = peerId;
    }

    public PollingMessageBase clone(){
        PollingMessageBase pollingMessageBase = new PollingMessageBase();
        pollingMessageBase.messageId = this.messageId;
        pollingMessageBase.flags = this.flags;
        pollingMessageBase.peerId = this.peerId;

        return pollingMessageBase;
    }

    public PollingMessageBase() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(messageId);
        dest.writeInt(flags);
        dest.writeInt(peerId);
    }

    protected PollingMessageBase(Parcel in) {
        messageId = in.readInt();
        flags = in.readInt();
        peerId = in.readInt();
    }

    public static final Creator<PollingMessageBase> CREATOR = new Creator<PollingMessageBase>() {
        @Override
        public PollingMessageBase createFromParcel(Parcel in) {
            return new PollingMessageBase(in);
        }

        @Override
        public PollingMessageBase[] newArray(int size) {
            return new PollingMessageBase[size];
        }
    };
}
