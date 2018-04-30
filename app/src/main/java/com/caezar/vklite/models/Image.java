package com.caezar.vklite.models;

/**
 * Created by seva on 13.04.18 in 15:50.
 */

@SuppressWarnings({"unused"})
public class Image {
    private String url;
    private int width;
    private int height;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Image() {
    }
}
