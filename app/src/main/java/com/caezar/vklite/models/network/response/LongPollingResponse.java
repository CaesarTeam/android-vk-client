package com.caezar.vklite.models.network.response;

/**
 * Created by seva on 28.05.18 in 21:12.
 */

public class LongPollingResponse {
    private int ts;
    private int pts;
    private Object[][] updates;

    public int getTs() {
        return ts;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public int getPts() {
        return pts;
    }

    public void setPts(int pts) {
        this.pts = pts;
    }

    public Object[][] getUpdates() {
        return updates;
    }

    public void setUpdates(Object[][] updates) {
        this.updates = updates;
    }

    public LongPollingResponse() {

    }
}
