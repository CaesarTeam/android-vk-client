package com.caezar.vklite.network;

import android.support.annotation.Nullable;

import com.caezar.vklite.network.modelsRequest.*;

/**
 * Created by seva on 25.03.18 in 18:11.
 */

public class urlBuilder {
    private final String version = "5.73";
    private final String host = "https://api.vk.com/method/";

    public String constructGetDialogs(Dialogs dialogs) {
        return constructUrl(Method.MESSAGES_GET_DIALOGS);
    }

    public String constructGetPrivateMessages(PrivateMessages privateMessages) {
        return "заглушка";
    }

    public String constructUrl(Method method, String... query) {
        final String url = host + method.getMethod() + "?" + query + "&access_token=" + "ACCESS_TOKEN" + "&v=" + version;
        return url;
    }

    private enum Method {
        MESSAGES_GET_DIALOGS("messages.getDialogs"); // Возвращает список диалогов текущего пользователя или сообщества.

        private String method;

        Method(String method) {
            this.method = method;
        }

        public String getMethod() {
            return method;
        }
    }
}
