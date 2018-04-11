package com.caezar.vklite.libs;

import android.widget.ImageView;

import com.caezar.vklite.R;
import com.squareup.picasso.Picasso;

/**
 * Created by seva on 10.04.18 in 16:38.
 */

public class ImageLoader {
    public static void asyncImageLoad(final String imageUrl, ImageView view) {
        Picasso.get()
               .load(imageUrl)
               .placeholder(R.drawable.default_avatar)
               .into(view);
    }

    public static void asyncImageLoad(final String imageUrl, ImageView view, int placeholderResId) {
        Picasso.get()
               .load(imageUrl)
               .placeholder(placeholderResId)
               .into(view);
    }

    public static String getUrlForResource(int resourceId) {
        return "android.resource://" + R.class.getPackage().getName() + "/" + resourceId;
    }
}
