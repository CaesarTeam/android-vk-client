package com.caezar.vklite.models.request;

/**
 * Created by seva on 02.04.18 in 21:31.
 */

@SuppressWarnings({"unused"})
public class UsersByIdRequest {
    private int[] user_ids;
    private String[] fields = new String[] {"photo_50", "photo_100", "photo_200", "photo_max"};

    public int[] getUser_ids() {
        return user_ids;
    }

    public void setUser_ids(int[] user_ids) {
        this.user_ids = user_ids;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public UsersByIdRequest() {

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int value : user_ids) {
            if (value > 0) {
                builder.append(value);
                builder.append(",");
            }
        }

        builder.setLength(builder.length() - 1);
        String user_ids = builder.toString();

        builder.setLength(0);

        for (String value : fields) {
            builder.append(value);
            builder.append(",");
        }

        builder.setLength(builder.length() - 1);
        String fields = builder.toString();

        return "&user_ids=" + user_ids +
                "&fields=" + fields;
    }
}
