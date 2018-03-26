package com.caezar.vklite.activities;

import android.content.Intent;
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

    private final NetworkManager.OnRequestCompleteListener listener =
            new NetworkManager.OnRequestCompleteListener() {
                @Override
                public void onRequestComplete(final String body) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.body.setText(body);
                            progress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            };

    private TextView body;
    private ProgressBar progress;

    final String[] scope = new String[] {VKScope.MESSAGES};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VKSdk.login(this, scope);

        Dialogs dialogs = new Dialogs();
        final String url = urlBuilder.constructGetDialogs(dialogs);
        Log.d("url", url);

        body = findViewById(R.id.body);
        progress = findViewById(R.id.progress);

        findViewById(R.id.user_agent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                body.setText("");
                progress.setVisibility(View.VISIBLE);
                NetworkManager.getInstance().get(url, listener);
            }
        });


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
