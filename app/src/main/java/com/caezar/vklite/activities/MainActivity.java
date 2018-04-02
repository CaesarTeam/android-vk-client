package com.caezar.vklite.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.caezar.vklite.R;
import com.caezar.vklite.network.Token;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "Vk";
    public static final String TOKEN = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        final String token = settings.getString(TOKEN, null);

        if (token == null) {
            final String[] scope = new String[] {VKScope.MESSAGES, VKScope.OFFLINE};
            VKSdk.login(this, scope);
        } else {
            Token.setToken(token);
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

                Token.setToken(res.accessToken);

                startActivity(new Intent(MainActivity.this, DialogsActivity.class));
            }
            @Override
            public void onError(VKError error) {
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
