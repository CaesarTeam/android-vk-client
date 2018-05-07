package com.caezar.vklite.models.network.response;

import com.caezar.vklite.models.network.DialogMessage;

/**
 * Created by seva on 25.03.18 in 18:41.
 */

@SuppressWarnings({"unused"})
public class ChatResponse {
    private Response response;

    public static class Response {
        private int count;
        private int unread;
        private DialogMessage[] items;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getUnread() {
            return unread;
        }

        public void setUnread(int unread) {
            this.unread = unread;
        }

        public DialogMessage[] getItems() {
            return items;
        }

        public void setItems(DialogMessage[] items) {
            this.items = items;
        }
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
