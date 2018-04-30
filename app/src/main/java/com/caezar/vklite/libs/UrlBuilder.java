package com.caezar.vklite.libs;

import com.caezar.vklite.Config;
import com.caezar.vklite.models.network.request.ChatRequest;
import com.caezar.vklite.models.network.request.DialogsRequest;
import com.caezar.vklite.models.network.request.EditMessageRequest;
import com.caezar.vklite.models.network.request.SendMessageRequest;
import com.caezar.vklite.models.network.request.UsersByIdRequest;
import com.caezar.vklite.models.network.request.UsersChatRequest;

/**
 * Created by seva on 25.03.18 in 18:11.
 */

public class UrlBuilder {
    private final static String version = "5.74";
    private final static String APIServiceAddressHost = "https://api.vk.com/method/";

    public static String constructGetDialogs(DialogsRequest dialogs) {
        return constructGetUrl(Method.MESSAGES_GET_DIALOGS, dialogs.toString());
    }

    public static String constructGetUsersInfo(UsersByIdRequest usersByid) {
        return constructGetUrl(Method.USERS_GET, usersByid.toString());
    }

    public static String constructGetChat(ChatRequest chatRequest) {
        return constructGetUrl(Method.MESSAGES_GET_HISTORY, chatRequest.toString());
    }

    public static String constructGetUsersChat(UsersChatRequest usersChatRequest) {
        return constructGetUrl(Method.MESSAGES_GET_CHAT, usersChatRequest.toString());
    }

    public static String constructSendMessage(SendMessageRequest sendMessageRequest) {
        return constructGetUrl(Method.MESSAGES_SEND, sendMessageRequest.toString());
    }

    public static String constructEditMessage(EditMessageRequest editMessageRequest) {
        return constructGetUrl(Method.MESSAGES_EDIT, editMessageRequest.toString());
    }

    public static String constructGetMyselfId() {
        return constructGetUrl(Method.USERS_GET, "");
    }

    private static String constructGetUrl(Method method, String query) {
        String baseUrl = constructUrl(method);
        return baseUrl + "?" + query + "&access_token=" + Config.getToken() + "&v=" + version;
    }

    private static String constructUrl(Method method) {
        return  APIServiceAddressHost + method.getMethod();
    }

    private enum Method {
        MESSAGES_SEND("messages.send"),
        MESSAGES_EDIT("messages.edit"),
        MESSAGES_GET_HISTORY("messages.getHistory"),
        MESSAGES_GET_DIALOGS("messages.getDialogs"),
        MESSAGES_GET_CHAT("messages.getChat"),
        USERS_GET("users.get");

        private final String method;

        Method(String method) {
            this.method = method;
        }

        public String getMethod() {
            return method;
        }
    }
}
