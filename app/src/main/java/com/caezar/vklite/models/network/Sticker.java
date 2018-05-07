package com.caezar.vklite.models.network;

/**
 * Created by seva on 13.04.18 in 15:48.
 */

@SuppressWarnings({"unused"})
public class Sticker {
    private int product_id;
    private int sticker_id;
    private Image[] images;
    private Image[] images_with_background;

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getSticker_id() {
        return sticker_id;
    }

    public void setSticker_id(int sticker_id) {
        this.sticker_id = sticker_id;
    }

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }

    public Image[] getImages_with_background() {
        return images_with_background;
    }

    public void setImages_with_background(Image[] images_with_background) {
        this.images_with_background = images_with_background;
    }

    public Sticker() {
    }
}