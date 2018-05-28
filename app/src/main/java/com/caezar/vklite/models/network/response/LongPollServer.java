package com.caezar.vklite.models.network.response;

/**
 * Created by seva on 28.05.18 in 20:13.
 */

public class LongPollServer {
    private Response response;

    public static class Response {
        private String key;
        private String server;
        private int ts;
        private int pts;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

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

        public Response() {

        }
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public LongPollServer() {

    }
}
/*
"response": {
"key": "48be0f1b08e9a2edfcf61f54f4092ddae1a4a13f",
"server": "imv4.vk.com/im0261",
"ts": 1746412102,
"pts": 10416217
}
 */