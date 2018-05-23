package com.caezar.vklite.managers;

import android.content.Context;
import android.util.Log;

import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.Func;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2.RequestOptions;

/**
 * Created by seva on 23.05.18 in 17:26.
 */

public class DownloadFilesManager {
    private static Fetch mainFetch;

    public static void initFetch(Context context) {
        mainFetch = new Fetch.Builder(context, "vkLite")
                .setDownloadConcurrentLimit(10)
                .enableLogging(true)
                .addRequestOptions(RequestOptions.ADD_AUTO_INCREMENT_TO_FILE_ON_ENQUEUE)
                .build();
    }

    public static void downloadFileFromUrl(String url, String name) {
        final Request request = new Request(url, name);
        Func<Download> func1 = download -> Log.d("func1", "call " + download.getUrl());
        Func<Error> func2 = error -> Log.d("func2", "call " + error.name());
        mainFetch.enqueue(request, func1, func2);
    }
}
