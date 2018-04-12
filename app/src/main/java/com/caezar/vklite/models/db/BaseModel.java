package com.caezar.vklite.models.db;

/**
 * Created by seva on 12.04.18 in 13:15.
 */

public class BaseModel {
    private int date;
    private Type type;

    public enum Type {
        DIALOG("dialog"),
        MESSAGE("message");

        private final String type;

        Type(String type) {
            this.type = type;
        }
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
