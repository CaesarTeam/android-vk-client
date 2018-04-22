package com.caezar.vklite;

import android.content.Context;
import android.support.annotation.NonNull;

import com.caezar.vklite.activities.DialogsActivity;
import com.caezar.vklite.libs.UrlBuilder;
import com.caezar.vklite.models.network.DialogItem;
import com.caezar.vklite.models.network.request.UsersByIdRequest;
import com.caezar.vklite.models.network.response.UsersByIdResponse;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.caezar.vklite.Config.ONLINE_MODE;
import static com.caezar.vklite.ErrorHandler.createErrorInternetToast;
import static com.caezar.vklite.ErrorHandler.makeToastError;
import static com.caezar.vklite.libs.ParseResponse.parseBody;
import static com.caezar.vklite.libs.Predicates.isEmptyTitle;
import static com.caezar.vklite.libs.Predicates.isPositiveUserId;

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

    public void requestGetUsers(int[] userIds, List<DialogItem> dialogs, Listener listenerCallback, Context context) {
        if (ONLINE_MODE) {
            final UsersByIdRequest usersByIdRequest = new UsersByIdRequest();
            usersByIdRequest.setUser_ids(userIds);
            final String url = UrlBuilder.constructGetUsersInfo(usersByIdRequest);
            NetworkManager.getInstance().get(url, new OnGetUsersComplete(dialogs, listenerCallback, context));
        } else {

        }
    }

    private class OnGetUsersComplete implements NetworkManager.OnRequestCompleteListener {
        private final Listener listenerCallback;
        private List<DialogItem> dialogs;
        private Context context;

        public OnGetUsersComplete(List<DialogItem> dialogs, Listener listenerCallback, Context context) {
            this.dialogs = dialogs;
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

            if (usersByIdResponse.getResponse() == null) {
                makeToastError(body, context);
                return;
            }

            dialogs = addDataToDialogsList(dialogs, usersByIdResponse);
            DialogsActivity.GetDialogs getDialogs = (DialogsActivity.GetDialogs) listenerCallback;
            getDialogs.callback(dialogs);

        }

        private List<DialogItem> addDataToDialogsList(List<DialogItem> dialogs, UsersByIdResponse usersByIdResponse) {
            Collection<DialogItem> privateDialogs = Collections2.filter(dialogs, Predicates.and(isEmptyTitle, isPositiveUserId));
            List<UsersByIdResponse.Response> users = Arrays.asList(usersByIdResponse.getResponse());

            for (DialogItem item: privateDialogs) {
                final int userId = item.getMessage().getUser_id();

                UsersByIdResponse.Response user = Iterables.find(users, _user -> _user.getId() == userId);

                item.getMessage().setTitle(user.getFirst_name() + " " + user.getLast_name());
                item.getMessage().setPhoto_50(user.getPhoto_50());
                item.getMessage().setPhoto_100(user.getPhoto_100());
                item.getMessage().setPhoto_200(user.getPhoto_200());
            }

            return dialogs;
        }
    }

}
