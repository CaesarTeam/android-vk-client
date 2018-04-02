package com.caezar.vklite.network;

import android.util.Log;

import com.caezar.vklite.network.models.DialogsRequest;
import com.caezar.vklite.network.models.UsersByIdRequest;
import com.caezar.vklite.network.modelsRequest.*;

/**
 * Created by seva on 25.03.18 in 18:11.
 */

public class urlBuilder {
    private final static String version = "5.74";
    private final static String APIServiceAddressHost = "https://api.vk.com/method/";

    public static String constructGetDialogs(DialogsRequest dialogs) {
        return constructUrl(Method.MESSAGES_GET_DIALOGS, dialogs.toString());
    }

    public static String constructGetUsersInfo(UsersByIdRequest usersByid) {
        return constructUrl(Method.USERS_GET, usersByid.toString());
    }

    public static String constructGetPrivateMessages(PrivateMessages privateMessages) {
        return "заглушка";
    }

    public static String constructUrl(Method method, String query) {
        final String url = APIServiceAddressHost + method.getMethod() + "?" + query + "&access_token=" + Token.getToken() + "&v=" + version;
        Log.d("constructUrl", url);
        return url;
    }

    private enum Method {
        MESSAGES_GET_DIALOGS("messages.getDialogs"), // Возвращает список диалогов текущего пользователя или сообщества.
        USERS_GET("users.get"); // Возвращает расширенную информацию о пользователях.

        private String method;

        Method(String method) {
            this.method = method;
        }

        public String getMethod() {
            return method;
        }
    }
}
