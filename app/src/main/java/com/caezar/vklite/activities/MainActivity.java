package com.caezar.vklite.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.caezar.vklite.R;
import com.caezar.vklite.network.MetaInfo;
import com.caezar.vklite.network.NetworkManager;
import com.caezar.vklite.network.models.UsersByIdResponse;
import com.caezar.vklite.network.urlBuilder;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "Vk";
    public static final String TOKEN = "token";
    public static final String MYSELF_ID = "myselfId";

    private final int WRONG_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
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
        MetaInfo.setToken(token);
        MetaInfo.setMyselfId(myselfId);
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

            final String url = urlBuilder.constructGetMyselfId();
            NetworkManager.getInstance().get(url, new OnLogInComplete(token));
        }
        @Override
        public void onError(VKError error) {
            // todo:
        }
    };

    private class OnLogInComplete implements NetworkManager.OnRequestCompleteListener {
        private String token;

        public OnLogInComplete(String token) {
            this.token = token;
        }

        @Override
        public void onRequestComplete(final String body) {
            int myselfId = getMyselfId(body);
            if (myselfId == WRONG_ID) {
                Log.e(this.getClass().getName(), "problems with get myselfId");
                return;
            }

            initMetaInfo(token, myselfId);

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(MYSELF_ID, myselfId);
            editor.putString(TOKEN, token);
            editor.apply();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, DialogsActivity.class));
                }
            });
        }

        private int getMyselfId(String body) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            TypeReference<UsersByIdResponse> mapType = new TypeReference<UsersByIdResponse>() {};
            UsersByIdResponse usersByIdResponse = new UsersByIdResponse();
            try {
                usersByIdResponse = mapper.readValue(body, mapType);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (usersByIdResponse != null) {
                return usersByIdResponse.getResponse()[0].getId();
            }

            return WRONG_ID;
        }
    }
}
