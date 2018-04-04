package com.caezar.vklite.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.caezar.vklite.R;
import com.caezar.vklite.network.MetaInfo;
import com.caezar.vklite.network.NetworkManager;
import com.caezar.vklite.network.models.DialogMessage;
import com.caezar.vklite.network.models.UsersByIdRequest;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "Vk";
    public static final String TOKEN = "token";
    public static final String MYSELF_ID = "myselfId";

    private final NetworkManager.OnRequestCompleteListener listenerId =
            new NetworkManager.OnRequestCompleteListener() {
                @Override
                public void onRequestComplete(final String body) {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            ObjectMapper mapper = new ObjectMapper();
                            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                            TypeReference<UsersByIdResponse> mapType = new TypeReference<UsersByIdResponse>() {};
                            UsersByIdResponse usersByIdResponse = new UsersByIdResponse();
                            try {
                                usersByIdResponse = mapper.readValue(body, mapType);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt(MYSELF_ID, usersByIdResponse.getResponse()[0].getId());
                            editor.apply();

                            MetaInfo.setMyselfId(usersByIdResponse.getResponse()[0].getId());

                            startActivity(new Intent(MainActivity.this, DialogsActivity.class));
                        }
                    });
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        final String token = settings.getString(TOKEN, null);
        final int myselfId = settings.getInt(MYSELF_ID, -1);

        if (token == null || myselfId == -1) {
            final String[] scope = new String[] {VKScope.MESSAGES, VKScope.OFFLINE};
            VKSdk.login(this, scope);
        } else {
            MetaInfo.setToken(token);
            MetaInfo.setMyselfId(myselfId);
            startActivity(new Intent(MainActivity.this, DialogsActivity.class));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(TOKEN, res.accessToken);
                editor.apply();

                MetaInfo.setToken(res.accessToken);

                final String url = urlBuilder.constructGetMyselfId();
                NetworkManager.getInstance().get(url, listenerId);
            }
            @Override
            public void onError(VKError error) {
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
