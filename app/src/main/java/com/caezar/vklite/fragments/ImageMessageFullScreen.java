package com.caezar.vklite.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.caezar.vklite.R;

import static com.caezar.vklite.activities.ChatActivity.PHOTO_URL;
import static com.caezar.vklite.libs.ImageLoader.asyncImageLoad;

/**
 * Created by seva on 10.04.18 in 12:56.
 */
public class ImageMessageFullScreen extends Fragment {

    private String urlPhoto;

    public ImageMessageFullScreen() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            urlPhoto = getArguments().getString(PHOTO_URL);
        }

        return inflater.inflate(R.layout.fragment_full_image, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (urlPhoto != null) {
            ImageView imageView = view.findViewById(R.id.fullSizeImage);
            asyncImageLoad(view.getContext(), urlPhoto, imageView);
        }
    }

}