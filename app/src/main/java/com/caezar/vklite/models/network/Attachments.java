package com.caezar.vklite.models.network;

/**
 * Created by seva on 11.04.18 in 11:56.
 */

@SuppressWarnings({"unused"})
public class Attachments {
    // todo: enum
    private String type;
    private Photo photo;
    private Sticker sticker;

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public Attachments() {

    }
}
