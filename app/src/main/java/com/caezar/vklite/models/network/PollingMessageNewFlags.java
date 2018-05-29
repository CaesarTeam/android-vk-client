package com.caezar.vklite.models.network;

/**
 * Created by seva on 29.05.18 in 21:03.
 */

public enum PollingMessageNewFlags {
    UNREAD(1),
    OUTBOX(2),
    REPLIED(4),
    IMPORTANT(8),
    CHAT(16),
    FRIENDS(32),
    SPAM(64),
    DELETED(128),
    FIXED(256),
    MEDIA(512),
    HIDDEN(65536),
    DELETED_FOR_ALL(131072);

    private final int flag;

    PollingMessageNewFlags(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }
}