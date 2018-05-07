package com.caezar.vklite.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.caezar.vklite.R;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import static com.caezar.vklite.fragments.ChatFragment.PHOTO_URL;
import static com.caezar.vklite.libs.ImageLoader.asyncImageLoad;

/**
 * Created by seva on 10.04.18 in 12:56.
 */

public class ImageMessageFullScreenFragment extends Fragment {
    public static final String IMAGE_FULL_FRAGMENT_TAG = "imageFullFragmentTag";

    private String urlPhoto;

    public ImageMessageFullScreenFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            urlPhoto = getArguments().getString(PHOTO_URL);
        }

        return inflater.inflate(R.layout.fragment_full_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        if (urlPhoto != null) {
            ImageViewTouch imageView = view.findViewById(R.id.fullSizeImage);
            asyncImageLoad(urlPhoto, imageView);
        }
    }

}