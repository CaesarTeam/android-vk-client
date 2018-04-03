package com.caezar.vklite.network;

import android.util.Log;

import com.caezar.vklite.network.models.ChatRequest;
import com.caezar.vklite.network.models.DialogsRequest;
import com.caezar.vklite.network.models.UsersByIdRequest;

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

    public static String constructGetChat(ChatRequest chatRequest) {
        return constructUrl(Method.MESSAGES_GET_HISTORY, chatRequest.toString());
    }

    public static String constructUrl(Method method, String query) {
        final String url = APIServiceAddressHost + method.getMethod() + "?" + query + "&access_token=" + Token.getToken() + "&v=" + version;
        Log.d("constructUrl", url);
        return url;
    }

    private enum Method {
        MESSAGES_GET_HISTORY("messages.getHistory"),
        MESSAGES_GET_DIALOGS("messages.getDialogs"),
        USERS_GET("users.get");

        private String method;

        Method(String method) {
            this.method = method;
        }

        public String getMethod() {
            return method;
        }
    }
}
