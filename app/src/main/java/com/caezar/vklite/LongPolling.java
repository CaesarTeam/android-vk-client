package com.caezar.vklite;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.caezar.vklite.models.network.response.LongPollingResponse;
import com.caezar.vklite.models.network.response.PollingMessageNewEdit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.caezar.vklite.fragments.ChatFragment.BROADCAST_EDIT_MESSAGE;
import static com.caezar.vklite.fragments.ChatFragment.BROADCAST_NEW_MESSAGE;
import static com.caezar.vklite.fragments.ChatFragment.EDIT_MESSAGE;
import static com.caezar.vklite.fragments.ChatFragment.NEW_MESSAGE;
import static com.caezar.vklite.helpers.LongPollingHelper.constructMessage;
import static com.caezar.vklite.helpers.LongPollingHelper.getLongPollingUrl;
import static com.caezar.vklite.libs.Jackson.parseBody;

/**
 * Created by seva on 28.05.18 in 20:37.
 */

public class LongPolling extends AsyncTask<String, Void, Void> {
    private String url;
    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .build();

    public LongPolling() {
        this.url = getLongPollingUrl();
    }

    @Override
    protected Void doInBackground(String... params) {
        final Request request = new Request.Builder().url(url).build();
        final Response response;
        try {
            response = client.newCall(request).execute();
            try (ResponseBody body = response.body()) {
                if (body != null) {
                    String responseString = body.string();
                    getResponse(responseString);
                }
            } catch (IOException e) {
//                e.printStackTrace();
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        new LongPolling().execute();
    }

    private void getResponse(String body) {
        Log.d("response", body);

        LongPollingResponse longPollingResponse = parseBody(LongPollingResponse.class, body);

        if (longPollingResponse == null || longPollingResponse.getUpdates() == null) {
            return;
        }

        Config.setTs(longPollingResponse.getTs());

        List<PollingMessageNewEdit> newMessageList = new ArrayList<>();
        List<PollingMessageNewEdit> editMessageList = new ArrayList<>();

        for (Object[] objects : longPollingResponse.getUpdates()) {
            if (objects.length < 1) {
                break;
            }

            try {
                int code = (int) objects[0];
                switch (code) {
                    case 4:
                        newMessageList.add(constructMessage(objects));
                        break;
                    case 5:
                        editMessageList.add(constructMessage(objects));
                        break;

                }

            } catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        if (newMessageList.size() > 0) {
            Intent intent = new Intent(BROADCAST_NEW_MESSAGE);
            intent.putParcelableArrayListExtra(NEW_MESSAGE, (ArrayList<? extends Parcelable>) newMessageList);
            if (Config.getApplicationContext() != null) {
                LocalBroadcastManager.getInstance(Config.getApplicationContext()).sendBroadcast(intent);
            }
        }

        if (editMessageList.size() > 0) {
            Intent intent = new Intent(BROADCAST_EDIT_MESSAGE);
            intent.putParcelableArrayListExtra(EDIT_MESSAGE, (ArrayList<? extends Parcelable>) editMessageList);
            if (Config.getApplicationContext() != null) {
                LocalBroadcastManager.getInstance(Config.getApplicationContext()).sendBroadcast(intent);
            }
        }

    }
}