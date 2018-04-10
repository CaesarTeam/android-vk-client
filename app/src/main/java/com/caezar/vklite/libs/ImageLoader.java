package com.caezar.vklite.libs;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.caezar.vklite.R;

/**
 * Created by seva on 10.04.18 in 16:38.
 */
public class ImageLoader {
    public static void asyncImageLoad(Context context, final String imageUrl, ImageView view) {
        Glide
            .with(context)
            .load(imageUrl)
            .into(view);
    }

    public static String getUrlForResource(int resourceId) {
        return "android.resource://" + R.class.getPackage().getName() + "/" + resourceId;
    }
}
