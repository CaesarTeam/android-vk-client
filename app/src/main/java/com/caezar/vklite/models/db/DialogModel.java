package com.caezar.vklite.models.db;

/**
 * Created by seva on 12.04.18 in 13:26.
 */

public class DialogModel extends BaseModel {
    private String title;
    private String message;
    private String imageUrl;
    private int peerId;

    public int getPeerId() {
        return peerId;
    }

    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public DialogModel() {
        setType(Type.DIALOG);
    }
}
