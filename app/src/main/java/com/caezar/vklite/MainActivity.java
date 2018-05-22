package com.caezar.vklite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.caezar.vklite.managers.NetworkManager;
import com.caezar.vklite.fragments.DialogsFragment;
import com.caezar.vklite.instanceState.DialogsInstanceState;
import com.caezar.vklite.models.network.response.UsersByIdResponse;
import com.caezar.vklite.libs.UrlBuilder;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import static com.caezar.vklite.fragments.DialogsFragment.CHAT_FRAGMENT_TAG;
import static com.caezar.vklite.helpers.ErrorHelper.createErrorInternetToast;
import static com.caezar.vklite.helpers.ErrorHelper.makeToastError;
import static com.caezar.vklite.libs.Jackson.parseBody;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "Vk";
    public static final String TOKEN = "token";
    private static final String MYSELF_ID = "myselfId";
    public static final String DIALOG_FRAGMENT_TAG = "dialogFragmentTag";

    @Nullable private OnBackPressedListener onBackPressedListener = null;

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null) {
            onBackPressedListener.doBack();
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.findViewById(R.id.toolbarBack).setOnClickListener((view) -> onBackPressed());
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final String token = settings.getString(TOKEN, null);
        final int myselfId = settings.getInt(MYSELF_ID, -1);

        if (!checkAuth(token, myselfId)) {
            logIn();
        } else {
            initMetaInfo(token, myselfId);
            openDialogs();
        }
    }

    public void setOnBackPressedListener(@Nullable OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    private boolean checkAuth(String token, int myselfId) {
        return !(token == null || myselfId == -1);
    }

    private void logIn() {
        final String[] scope = new String[] {VKScope.MESSAGES, VKScope.OFFLINE};
        VKSdk.login(this, scope);
    }

    private void initMetaInfo(String token, int myselfId) {
        Config.setToken(token);
        Config.setMyselfId(myselfId);
    }

    private void openDialogs() {
        if (getSupportFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) == null) {
            DialogsInstanceState.getInstance().reset();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.mainContainer, new DialogsFragment(), DIALOG_FRAGMENT_TAG);
            transaction.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, vkAccessTokenVKCallback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private final VKCallback<VKAccessToken> vkAccessTokenVKCallback = new VKCallback<VKAccessToken>() {
        @Override
        public void onResult(VKAccessToken res) {
            final String token = res.accessToken;

            final String url = UrlBuilder.constructGetMyselfId();
            NetworkManager.getInstance().get(url, new OnLogInComplete(token));
        }
        @Override
        public void onError(VKError error) {
            // todo:
        }
    };

    private class OnLogInComplete implements NetworkManager.OnRequestCompleteListener {
        private final String token;

        public OnLogInComplete(String token) {
            this.token = token;
        }

        @Override
        public void onError(String body) {
            createErrorInternetToast(MainActivity.this);
        }

        @Override
        public void onErrorCode(int code) {

        }

        @Override
        public void onResponse(final String body) {
            UsersByIdResponse usersByIdResponse = parseBody(UsersByIdResponse.class, body);

            if (usersByIdResponse == null) {
                makeToastError(body, MainActivity.this);
                return;
            }

            int myselfId = usersByIdResponse.getUsers()[0].getId();

            initMetaInfo(token, myselfId);

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(MYSELF_ID, myselfId);
            editor.putString(TOKEN, token);
            editor.apply();

            runOnUiThread(MainActivity.this::openDialogs);
        }
    }
}
