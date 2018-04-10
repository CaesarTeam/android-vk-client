package com.caezar.vklite.libs;

import com.caezar.vklite.MetaInfo;
import com.caezar.vklite.models.request.ChatRequest;
import com.caezar.vklite.models.request.DialogsRequest;
import com.caezar.vklite.models.request.SendMessageRequest;
import com.caezar.vklite.models.request.UsersByIdRequest;

/**
 * Created by seva on 25.03.18 in 18:11.
 */

public class urlBuilder {
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

    public static String constructSendMessage(SendMessageRequest sendMessageRequest) {
        return constructGetUrl(Method.MESSAGES_SEND, sendMessageRequest.toString());
    }

    public static String constructGetMyselfId() {
        return constructGetUrl(Method.USERS_GET, "");
    }

    private static String constructGetUrl(Method method, String query) {
        String baseUrl = constructUrl(method);
        return baseUrl + "?" + query + "&access_token=" + MetaInfo.getToken() + "&v=" + version;
    }

    private static String constructUrl(Method method) {
        return  APIServiceAddressHost + method.getMethod();
    }

    private enum Method {
        MESSAGES_SEND("messages.send"),
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
