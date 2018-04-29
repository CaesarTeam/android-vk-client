package com.caezar.vklite;

import android.content.Context;
import android.support.annotation.NonNull;

import com.caezar.vklite.libs.UrlBuilder;
import com.caezar.vklite.models.network.User;
import com.caezar.vklite.models.network.request.UsersByIdRequest;
import com.caezar.vklite.models.network.request.UsersChatRequest;
import com.caezar.vklite.models.network.response.UsersByIdResponse;
import com.caezar.vklite.models.network.response.UsersChatResponse;

import static com.caezar.vklite.Config.ONLINE_MODE;
import static com.caezar.vklite.ErrorHandler.createErrorInternetToast;
import static com.caezar.vklite.ErrorHandler.makeToastError;
import static com.caezar.vklite.libs.ParseResponse.parseBody;

/**
 * Created by seva on 22.04.18 in 8:35.
 */

public class UserManager {
    @NonNull
    private final static UserManager INSTANCE = new UserManager();

    private UserManager() {
    }

    public static UserManager getInstance() {
        return INSTANCE;
    }

    public void getUsers(int[] userIds, GetUsers listenerCallback, Context context) {
        if (ONLINE_MODE) {
            final UsersByIdRequest usersByIdRequest = new UsersByIdRequest();
            usersByIdRequest.setUser_ids(userIds);
            final String url = UrlBuilder.constructGetUsersInfo(usersByIdRequest);
            NetworkManager.getInstance().get(url, new OnGetUsersComplete(listenerCallback, context));
        } else {

        }
    }

    public void getUsersChat(int chatId, GetUserIds listenerCallback, Context context) {
        if (ONLINE_MODE) {
            UsersChatRequest usersChatRequest = new UsersChatRequest();
            usersChatRequest.setChat_id(chatId);
            final String url = UrlBuilder.constructGetUsersChat(usersChatRequest);
            NetworkManager.getInstance().get(url, new OnGetUsersIdComplete(listenerCallback, context));
        } else {

        }
    }

    private class OnGetUsersComplete implements NetworkManager.OnRequestCompleteListener {
        private final GetUsers listenerCallback;
        private Context context;

        public OnGetUsersComplete(GetUsers listenerCallback, Context context) {
            this.listenerCallback = listenerCallback;
            this.context = context;
        }

        @Override
        public void onError(String body) {
            createErrorInternetToast(context);
        }

        @Override
        public void onErrorCode(int code) {
        }

        @Override
        public void onResponse(final String body) {
            UsersByIdResponse usersByIdResponse = parseBody(UsersByIdResponse.class, body);

            if (usersByIdResponse.getUsers() == null) {
                makeToastError(body, context);
                return;
            }

            listenerCallback.callback(usersByIdResponse.getUsers());
        }
    }

    private class OnGetUsersIdComplete implements NetworkManager.OnRequestCompleteListener {
        private final GetUserIds listenerCallback;
        private Context context;

        public OnGetUsersIdComplete(GetUserIds listenerCallback, Context context) {
            this.listenerCallback = listenerCallback;
            this.context = context;
        }

        @Override
        public void onError(String body) {
            createErrorInternetToast(context);
        }

        @Override
        public void onErrorCode(int code) {
        }

        @Override
        public void onResponse(final String body) {
            UsersChatResponse usersChatResponse = parseBody(UsersChatResponse.class, body);

            if (usersChatResponse.getResponse() == null) {
                makeToastError(body, context);
                return;
            }

            final int[] usersId = usersChatResponse.getResponse().getUsers();
            listenerCallback.callback(usersId);
        }
    }

    public interface GetUsers extends Listener {
        void callback(User[] users);
    }

    public interface GetUserIds extends Listener {
        void callback(int[] userIds);
    }

}
