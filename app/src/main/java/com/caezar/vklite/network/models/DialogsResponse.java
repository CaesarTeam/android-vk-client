package com.caezar.vklite.network.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Arrays;

/**
 * Created by seva on 01.04.18 in 20:03.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class DialogsResponse {
    private Response response;

    public static class Response {
        private int count;

        private int  unread_dialogs;

        private DialogItem[] items;

        public static class DialogItem {
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
        }

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
