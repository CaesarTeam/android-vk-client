package com.caezar.vklite.models.network.request;

/**
 * Created by seva on 01.05.18 in 16:20.
 */

public class DeleteMessageRequest {
    private int[] message_ids;
    private boolean delete_for_all = true;

    public int[] getMessage_ids() {
        return message_ids;
    }

    public void setMessage_ids(int[] message_ids) {
        this.message_ids = message_ids;
    }

    public boolean isDelete_for_all() {
        return delete_for_all;
    }

    public void setDelete_for_all(boolean delete_for_all) {
        this.delete_for_all = delete_for_all;
    }

    public DeleteMessageRequest() {

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int value: message_ids) {
            builder.append(value).append(",");
        }

        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }

        String message_ids = builder.toString();

        return  "&message_ids=" + message_ids +
                "&delete_for_all=" + delete_for_all;
    }
}
