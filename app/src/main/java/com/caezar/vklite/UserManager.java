package com.caezar.vklite;

import android.content.Context;
import android.support.annotation.NonNull;

import com.caezar.vklite.libs.UrlBuilder;
import com.caezar.vklite.models.network.User;
import com.caezar.vklite.models.network.request.UsersByIdRequest;
import com.caezar.vklite.models.network.response.UsersByIdResponse;

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

    public void requestGetUsers(int[] userIds, GetUsers listenerCallback, Context context) {
        if (ONLINE_MODE) {
            final UsersByIdRequest usersByIdRequest = new UsersByIdRequest();
            usersByIdRequest.setUser_ids(userIds);
            final String url = UrlBuilder.constructGetUsersInfo(usersByIdRequest);
            NetworkManager.getInstance().get(url, new OnGetUsersComplete(listenerCallback, context));
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

    public interface GetUsers extends Listener {
        void callback(User[] users);
    }

}
