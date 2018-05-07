package com.caezar.vklite.models.network.response;

import com.caezar.vklite.models.network.DialogItem;

import java.util.Arrays;

/**
 * Created by seva on 01.04.18 in 20:03.
 */

@SuppressWarnings({"unused"})
public class DialogsResponse {
    private Response response;

    public static class Response {
        private int count;

        private int  unread_dialogs;

        private DialogItem[] items;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getUnread_dialogs() {
            return unread_dialogs;
        }

        public void setUnread_dialogs(int unread_dialogs) {
            this.unread_dialogs = unread_dialogs;
        }

        public DialogItem[] getItems() {
            return items;
        }

        public void setItems(DialogItem[] items) {
            this.items = items;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "count=" + count +
                    ", unread_dialogs=" + unread_dialogs +
                    ", items=" + Arrays.toString(items) +
                    '}';
        }
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public DialogsResponse() {
    }

    @Override
    public String toString() {
        return "DialogsResponse{" +
                "response=" + response +
                '}';
    }
}
