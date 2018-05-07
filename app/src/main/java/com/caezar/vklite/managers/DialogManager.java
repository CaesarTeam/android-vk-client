package com.caezar.vklite.managers;

import android.content.Context;
import android.support.annotation.NonNull;

import com.caezar.vklite.Listener;
import com.caezar.vklite.libs.Db;
import com.caezar.vklite.libs.UrlBuilder;
import com.caezar.vklite.models.network.DialogItem;
import com.caezar.vklite.models.network.request.DialogsRequest;
import com.caezar.vklite.models.network.response.DialogsResponse;

import java.util.Arrays;
import java.util.List;

import static com.caezar.vklite.Config.ONLINE_MODE;
import static com.caezar.vklite.Config.countItemsToRequestDialogs;
import static com.caezar.vklite.helpers.ErrorHelper.createErrorInternetToast;
import static com.caezar.vklite.helpers.ErrorHelper.makeToastError;
import static com.caezar.vklite.libs.Jackson.parseBody;

/**
 * Created by seva on 21.04.18 in 23:10.
 */

public final class DialogManager {

    @NonNull private final static DialogManager INSTANCE = new DialogManager();

    private DialogManager() {
    }

    public static DialogManager getInstance() {
        return INSTANCE;
    }

    public void getDialogs(int offset, GetDialogs listener, Context context) {
        if (ONLINE_MODE) {
            DialogsRequest dialogsRequest = new DialogsRequest();
            dialogsRequest.setOffset(offset);
            final String url = UrlBuilder.constructGetDialogs(dialogsRequest);
            NetworkManager.getInstance().get(url, new OnGetDialogsComplete(listener, context));
        } else {
            Db.getDialogs(listener, countItemsToRequestDialogs, offset);
        }
    }

    private class OnGetDialogsComplete implements NetworkManager.OnRequestCompleteListener {
        private final GetDialogs listenerCallback;
        private final Context context;

        public OnGetDialogsComplete(GetDialogs listenerCallback, Context context) {
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
            DialogsResponse dialogsResponse = parseBody(DialogsResponse.class, body);

            if (dialogsResponse.getResponse() == null) {
                makeToastError(body, context);
                return;
            }

            final List<DialogItem> dialogs = Arrays.asList(dialogsResponse.getResponse().getItems());
            listenerCallback.callback(dialogs);
        }
    }

    public interface GetDialogs extends Listener {
        void callback(List<DialogItem> dialogs);
    }

}

