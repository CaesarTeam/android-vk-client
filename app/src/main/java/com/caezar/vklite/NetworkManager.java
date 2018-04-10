package com.caezar.vklite;

/**
 * Created by seva on 25.03.18 in 18:09.
 */

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetworkManager {

    private static final NetworkManager INSTANCE = new NetworkManager();
    private static final String TAG = "NETWORK";

    private final OkHttpClient client = new OkHttpClient();
    private final Executor executor = Executors.newSingleThreadExecutor();

    private NetworkManager() {
    }

    public static NetworkManager getInstance() {
        return INSTANCE;
    }

    public void get(final String url, final OnRequestCompleteListener listener) {
        Log.d("request", url);
        final Request request = new Request.Builder()
                .url(url)
                .build();
        performRequest(request, listener);
    }

    private void performRequest(final Request request, final OnRequestCompleteListener listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final String body = getBody(request);
                Log.d("response", body);
                listener.onResponse(body);
            }
        });
    }

    private String getBody(final Request request) {
        try {
            final Response response = client.newCall(request).execute();
            try (ResponseBody body = response.body()) {
                if (response.isSuccessful() && body != null) {
                    return body.string();
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Fail to perform request", e);
        }
        return null;
    }

    public interface OnRequestCompleteListener {
        void onResponse(final String body);
    }

    // todo: add class here with method check body null. extends implemenst. if null fragment with return. on close fragment try request again
}
