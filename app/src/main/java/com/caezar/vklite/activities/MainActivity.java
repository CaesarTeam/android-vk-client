package com.caezar.vklite.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.caezar.vklite.R;
import com.caezar.vklite.network.Token;
import com.caezar.vklite.network.modelsRequest.Dialogs;
import com.caezar.vklite.network.urlBuilder;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

public class MainActivity extends AppCompatActivity {
    final String[] scope = new String[] {VKScope.MESSAGES};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("MainActivity");
        setContentView(R.layout.activity_main);

        VKSdk.login(this, scope);

        Dialogs dialogs = new Dialogs();
        final String url = urlBuilder.constructGetDialogs(dialogs);

        final TextView textView = findViewById(R.id.text123);
        textView.setText(url);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Token.setToken(res.accessToken);
                    // Пользователь успешно авторизовался
            }
            @Override
            public void onError(VKError error) {
                    // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
