package com.caezar.vklite.models.network;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by seva on 14.04.18 in 13:57.
 */

@SuppressWarnings({"unused"})
public class Document {
    private int id;
    private int owner_id;
    private String title;
    private long size;
    private String ext;
    private String url;
    private String access_key;
    private int date;
    // todo: use enum
    private int type;

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum TypeDoc {
        @JsonProperty("1")
        TEXT(1),
        @JsonProperty("2")
        RAR(2),
        @JsonProperty("3")
        GIF(3),
        @JsonProperty("4")
        IMAGE(4),
        @JsonProperty("5")
        AUDIO(5),
        @JsonProperty("6")
        VIDEO(6),
        @JsonProperty("7")
        BOOK(7),
        @JsonProperty("8")
        UNKNOWN(8);

        private final int typeDoc;

        TypeDoc(int typeDoc) {
            this.typeDoc = typeDoc;
        }
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccess_key() {
        return access_key;
    }

    public void setAccess_key(String access_key) {
        this.access_key = access_key;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Document() {
    }
}