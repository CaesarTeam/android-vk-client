package com.caezar.vklite.helpers;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caezar.vklite.R;

/**
 * Created by seva on 06.05.18 in 21:27.
 */

public class ToolbarHelper {
    //todo: ask it
    public static void setToolbarTitle(Toolbar toolbar, String title) {
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(title);
    }

    public static void showToolbarBack(Toolbar toolbar) {
        ImageView toolbarBack = toolbar.findViewById(R.id.toolbarBack);
        toolbarBack.setVisibility(View.VISIBLE);
    }

    public static void hideToolbarBack(Toolbar toolbar) {
        ImageView toolbarBack = toolbar.findViewById(R.id.toolbarBack);
        toolbarBack.setVisibility(View.INVISIBLE);
    }
}
