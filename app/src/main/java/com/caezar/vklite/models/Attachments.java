package com.caezar.vklite.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by seva on 11.04.18 in 11:56.
 */

@SuppressWarnings({"unused"})
public class Attachments {
    private MessageType type;
    private Photo photo;
    private Sticker sticker;
    private Document doc;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Sticker getSticker() {
        return sticker;
    }

    public void setSticker(Sticker sticker) {
        this.sticker = sticker;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public Attachments() {

    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum MessageType {
        @JsonProperty("doc")
        DOC("doc"),
        @JsonProperty("photo")
        PHOTO("photo"),
        @JsonProperty("video")
        VIDEO("video"),
        @JsonProperty("audio")
        AUDIO("audio"),
        @JsonProperty("link")
        LINK("link"),
        @JsonProperty("market")
        MARKET("market"),
        @JsonProperty("market_album")
        MARKET_ALBUM("market_album"),
        @JsonProperty("wall_reply")
        WALL_REPLY("wall_reply"),
        @JsonProperty("wall")
        WALL("wall"),
        @JsonProperty("gift")
        GIFT("gift"),
        @JsonProperty("sticker")
        STICKER("sticker");

        private final String messageType;

        MessageType(String messageType) {
            this.messageType = messageType;
        }
    }
}