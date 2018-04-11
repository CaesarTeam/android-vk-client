package com.caezar.vklite.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caezar.vklite.R;

/**
 * Created by seva on 11.04.18 in 13:00.
 */

public class ErrorInternetFragment extends Fragment {

    public ErrorInternetFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_error_internet, container, false);
    }
}
