package com.caezar.vklite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.caezar.vklite.managers.NetworkManager;
import com.caezar.vklite.fragments.DialogsFragment;
import com.caezar.vklite.instanceState.DialogsInstanceState;
import com.caezar.vklite.models.network.response.LongPollServer;
import com.caezar.vklite.models.network.response.UsersByIdResponse;
import com.caezar.vklite.libs.UrlBuilder;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import static com.caezar.vklite.Config.LONG_POLLING_ENABLE;
import static com.caezar.vklite.helpers.ErrorHelper.createErrorInternetToast;
import static com.caezar.vklite.helpers.ErrorHelper.makeToastError;
import static com.caezar.vklite.libs.Jackson.parseBody;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "Vk";
    public static final String TOKEN = "token";
    public static final String MYSELF_ID = "myselfId";
    public static final String DIALOG_FRAGMENT_TAG = "dialogFragmentTag";

    private String token;
    private int myselfId;

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

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        token = settings.getString(TOKEN, null);
        myselfId = settings.getInt(MYSELF_ID, -1);

        if (token == null || myselfId == -1) {
            VKSdk.login(this, VKScope.MESSAGES, VKScope.OFFLINE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (token != null || myselfId != -1) {
            initMetaInfo();
            if (LONG_POLLING_ENABLE) {
                final String url = UrlBuilder.constructGetLongPollServer();
                NetworkManager.getInstance().get(url, new OnGetLongPollServer());
            }

            openDialogs();
        }
    }

    public void setOnBackPressedListener(@Nullable OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    private void initMetaInfo() {
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
            token = res.accessToken;
            final String url = UrlBuilder.constructGetMyselfId();
            NetworkManager.getInstance().get(url, new OnLogInComplete());
        }
        @Override
        public void onError(VKError error) {
            Log.d("MainActivity", "Error with vkAccessTokenVKCallback");
            // todo:
        }
    };

    private class OnLogInComplete implements NetworkManager.OnRequestCompleteListener {
        OnLogInComplete() {

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

            myselfId = usersByIdResponse.getUsers()[0].getId();
            initMetaInfo();

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(MYSELF_ID, myselfId);
            editor.putString(TOKEN, token);
            editor.apply();

            if (LONG_POLLING_ENABLE) {
                final String url = UrlBuilder.constructGetLongPollServer();
                NetworkManager.getInstance().get(url, new OnGetLongPollServer());
            }

            runOnUiThread(MainActivity.this::openDialogs);
        }
    }

    private class OnGetLongPollServer implements NetworkManager.OnRequestCompleteListener {
        OnGetLongPollServer() {

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
            LongPollServer longPollServer = parseBody(LongPollServer.class, body);

            if (longPollServer.getResponse() == null) {
                makeToastError(body, MainActivity.this);
                return;
            }

            Config.setKey(longPollServer.getResponse().getKey());
            Config.setServer(longPollServer.getResponse().getServer());
            Config.setTs(longPollServer.getResponse().getTs());
            Config.setPts(longPollServer.getResponse().getPts());

            new LongPolling().execute();
        }
    }

}
