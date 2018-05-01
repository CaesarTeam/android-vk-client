package com.caezar.vklite;

import android.content.SharedPreferences;

import com.caezar.vklite.libs.ParseResponse;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import static com.caezar.vklite.activities.MainActivity.PREFS_NAME;
import static com.caezar.vklite.activities.MainActivity.TOKEN;

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

        ParseResponse.configureInstance();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
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