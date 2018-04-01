package com.caezar.vklite.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caezar.vklite.R;
import com.caezar.vklite.network.NetworkManager;
import com.caezar.vklite.network.Token;
import com.caezar.vklite.network.modelsRequest.Dialogs;
import com.caezar.vklite.network.urlBuilder;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "Vk";

    private final NetworkManager.OnRequestCompleteListener listener =
            new NetworkManager.OnRequestCompleteListener() {
                @Override
                public void onRequestComplete(final String body) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.body.setText(body);
                        }
                    });
                }
            };

    private TextView body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        final String token = settings.getString("token", null);

        if (token == null) {
            final String[] scope = new String[] {VKScope.MESSAGES, VKScope.OFFLINE};
            VKSdk.login(this, scope);
        } else {
            Token.setToken(token);
        }


        body = findViewById(R.id.body);

        findViewById(R.id.user_agent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                body.setText("");
                Dialogs dialogs = new Dialogs();
                final String url = urlBuilder.constructGetDialogs(dialogs);
                NetworkManager.getInstance().get(url, listener);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("token", res.accessToken);
                editor.apply();

                Token.setToken(res.accessToken);
            }
            @Override
            public void onError(VKError error) {
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
