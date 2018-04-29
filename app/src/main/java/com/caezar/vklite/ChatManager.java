package com.caezar.vklite;

import android.content.Context;
import android.support.annotation.NonNull;

import com.caezar.vklite.activities.ChatActivity;
import com.caezar.vklite.libs.UrlBuilder;
import com.caezar.vklite.models.network.DialogItem;
import com.caezar.vklite.models.network.DialogMessage;
import com.caezar.vklite.models.network.request.ChatRequest;
import com.caezar.vklite.models.network.request.DialogsRequest;
import com.caezar.vklite.models.network.response.ChatResponse;
import com.caezar.vklite.models.network.response.DialogsResponse;

import java.util.Arrays;
import java.util.List;

import static com.caezar.vklite.Config.ONLINE_MODE;
import static com.caezar.vklite.ErrorHandler.createErrorInternetToast;
import static com.caezar.vklite.ErrorHandler.makeToastError;
import static com.caezar.vklite.libs.Db.insertDialogs;
import static com.caezar.vklite.libs.ParseResponse.parseBody;

/**
 * Created by seva on 29.04.18 in 18:51.
 */

public class ChatManager {
    @NonNull
    private final static ChatManager INSTANCE = new ChatManager();

    private ChatManager() {
    }

    public static ChatManager getInstance() {
        return INSTANCE;
    }

    public void getChat(int offset, int peer_id, int count, Listener listener, Context context) {
        if (ONLINE_MODE) {
            final ChatRequest chatRequest = new ChatRequest();
            chatRequest.setCount(count);
            chatRequest.setOffset(offset);
            chatRequest.setPeer_id(peer_id);
            final String url = UrlBuilder.constructGetChat(chatRequest);
            NetworkManager.getInstance().get(url, new ChatManager.OnGetMessagesComplete((ChatManager.GetMessages)listener, context));
        } else {

        }
    }

    private class OnGetMessagesComplete implements NetworkManager.OnRequestCompleteListener {
        private final ChatManager.GetMessages listenerCallback;
        private final Context context;

        public OnGetMessagesComplete(ChatManager.GetMessages listenerCallback, Context context) {
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
            ChatResponse chatResponse = parseBody(ChatResponse.class, body);

            if (chatResponse.getResponse() == null) {
                makeToastError(body, context);
                return;
            }

            final List<DialogMessage> messages = Arrays.asList(chatResponse.getResponse().getItems());
//            insertDialogs(DbManager.getInstance(context), dialogs);
            listenerCallback.callback(messages);
        }
    }

    public interface GetMessages extends Listener {
        void callback(List<DialogMessage> messages);
    }
}
