package com.caezar.vklite.models.network.request;

/**
 * Created by seva on 02.04.18 in 21:31.
 */

@SuppressWarnings({"unused"})
public class UsersByIdRequest {
    private int[] user_ids;
    private String fields = "photo_50,photo_100,photo_200,photo_max,online";

    public int[] getUser_ids() {
        return user_ids;
    }

    public void setUser_ids(int[] user_ids) {
        this.user_ids = user_ids;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public UsersByIdRequest() {

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int value: user_ids) {
            builder.append(value).append(",");
        }

        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }

        String user_ids = builder.toString();

        return "&user_ids=" + user_ids +
                "&fields=" + fields;
    }
}
