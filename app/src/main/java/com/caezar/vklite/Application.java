package com.caezar.vklite;

import android.content.SharedPreferences;

import com.caezar.vklite.libs.Jackson;
import com.caezar.vklite.managers.DbManager;
import com.caezar.vklite.managers.DownloadFilesManager;
import com.squareup.leakcanary.LeakCanary;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import static com.caezar.vklite.MainActivity.MYSELF_ID;
import static com.caezar.vklite.MainActivity.PREFS_NAME;
import static com.caezar.vklite.MainActivity.TOKEN;

/**
 * Created by seva on 26.03.18 in 1:50.
 */

public class Application extends android.app.Application {

    private final VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                // todo:
            } else {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(TOKEN, newToken.accessToken);
                editor.apply();

                Config.setToken(newToken.accessToken);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final String token = settings.getString(TOKEN, null);
        final int myselfId = settings.getInt(MYSELF_ID, -1);
        Config.setToken(token);
        Config.setMyselfId(myselfId);

        DownloadFilesManager.initFetch(getApplicationContext());
        DbManager.getInstance().setContext(getApplicationContext());
        Jackson.configureInstance();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
/*
material design HIGH PRIORITY
0) Добавить read state в чат
1) пометить сообщение как важное
2) Ответить, переслать сообщение
3) Отображать пересланные сообщения
4) Отображать запись со стены
6) Сделать выход из аккаунта
7) Сделать меню настроек
10) Скачивать документ
12) Автоматическое обновление
11) Уведомления
13) Показывать что человек пишет
9) Научить сохранять картинки на телефон
) прикрутить нормальные смайлики
) добавить отправку стикеров
) при промотке в чате сверху дата как в оригинальном вк
VK evolution
5) Доделать базу
в меню настроек чтобы было добавить всё
добавить диалог целиком/последнии n сообщений из m
из настроек посмотреть статистику
 */