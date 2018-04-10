package com.caezar.vklite;

import android.content.SharedPreferences;

import com.caezar.vklite.libs.ParseResponse;
import com.caezar.vklite.network.MetaInfo;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import static com.caezar.vklite.activities.MainActivity.PREFS_NAME;
import static com.caezar.vklite.activities.MainActivity.TOKEN;

/**
 * Created by seva on 26.03.18 in 1:50.
 */

public class Application extends android.app.Application {

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                // todo:
            } else {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(TOKEN, newToken.accessToken);
                editor.apply();

                MetaInfo.setToken(newToken.accessToken);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        ParseResponse.initInstance();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }
}
