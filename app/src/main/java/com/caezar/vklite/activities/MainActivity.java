package com.caezar.vklite.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.caezar.vklite.R;
import com.caezar.vklite.Config;
import com.caezar.vklite.NetworkManager;
import com.caezar.vklite.models.network.response.UsersByIdResponse;
import com.caezar.vklite.libs.UrlBuilder;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import static com.caezar.vklite.ErrorHandler.createErrorInternetToast;
import static com.caezar.vklite.ErrorHandler.makeToastError;
import static com.caezar.vklite.libs.ParseResponse.parseBody;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "Vk";
    public static final String TOKEN = "token";
    private static final String MYSELF_ID = "myselfId";

    private final int WRONG_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final String token = settings.getString(TOKEN, null);
        final int myselfId = settings.getInt(MYSELF_ID, WRONG_ID);

        if (!checkAuth(token, myselfId)) {
            logIn();
        } else {
            initMetaInfo(token, myselfId);
            startActivity(new Intent(MainActivity.this, DialogsActivity.class));
        }
    }

    private boolean checkAuth(String token, int myselfId) {
        return !(token == null || myselfId == WRONG_ID);
    }

    private void logIn() {
        final String[] scope = new String[] {VKScope.MESSAGES, VKScope.OFFLINE};
        VKSdk.login(this, scope);
    }

    private void initMetaInfo(String token, int myselfId) {
        Config.setToken(token);
        Config.setMyselfId(myselfId);
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
            int myselfId = getMyselfId(body);
            if (myselfId == WRONG_ID) {
                Log.e(this.getClass().getName(), "problems with get myselfId");
                return;
            }

            initMetaInfo(token, myselfId);

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(MYSELF_ID, myselfId);
            editor.putString(TOKEN, token);
            editor.apply();

            runOnUiThread(() -> startActivity(new Intent(MainActivity.this, DialogsActivity.class)));
        }

        private int getMyselfId(String body) {
            UsersByIdResponse usersByIdResponse = parseBody(UsersByIdResponse.class, body);

            if (usersByIdResponse == null) {
                makeToastError(body, MainActivity.this);
                return WRONG_ID;
            }

            return usersByIdResponse.getResponse()[0].getId();
        }
    }
}
