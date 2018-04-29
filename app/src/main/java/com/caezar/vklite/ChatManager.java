package com.caezar.vklite;

import android.content.Context;
import android.support.annotation.NonNull;

import com.caezar.vklite.libs.UrlBuilder;
import com.caezar.vklite.models.network.DialogMessage;
import com.caezar.vklite.models.network.request.ChatRequest;
import com.caezar.vklite.models.network.request.SendMessageRequest;
import com.caezar.vklite.models.network.response.ChatResponse;
import com.caezar.vklite.models.network.response.SendResponse;

import java.util.Arrays;
import java.util.List;

import static com.caezar.vklite.Config.ONLINE_MODE;
import static com.caezar.vklite.ErrorHandler.createErrorInternetToast;
import static com.caezar.vklite.ErrorHandler.makeToastError;
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

    public void sendMessage(String message, int peer_id, Listener listener, Context context) {
        if (ONLINE_MODE) {
            final SendMessageRequest sendMessageRequest = new SendMessageRequest();
            sendMessageRequest.setMessage(message);
            sendMessageRequest.setPeer_id(peer_id);
            final String url = UrlBuilder.constructSendMessage(sendMessageRequest);
            NetworkManager.getInstance().get(url, new OnSendMessageComplete((ChatManager.SendMessage)listener, context));
        } else {
            // todo: save to store to send when online
        }
    }

    public void getChat(int offset, int peer_id, int count, Listener listener, Context context) {
        if (ONLINE_MODE) {
            final ChatRequest chatRequest = new ChatRequest();
            chatRequest.setCount(count);
            chatRequest.setOffset(offset);
            chatRequest.setPeer_id(peer_id);
            final String url = UrlBuilder.constructGetChat(chatRequest);
            NetworkManager.getInstance().get(url, new ChatManager.OnGetMessagesComplete(listener, context));
        } else {

        }
    }

    private class OnGetMessagesComplete implements NetworkManager.OnRequestCompleteListener {
        private final Listener listenerCallback;
        private final Context context;

        public OnGetMessagesComplete(Listener listenerCallback, Context context) {
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
            if (listenerCallback instanceof GetMessages) {
                ((GetMessages)listenerCallback).callback(messages);
            } else if (listenerCallback instanceof GetLastMessage) {
                ((GetLastMessage)listenerCallback).callback(messages.get(0));
            }
        }
    }

    private class OnSendMessageComplete implements NetworkManager.OnRequestCompleteListener {
        private final ChatManager.SendMessage listenerCallback;
        private final Context context;

        public OnSendMessageComplete(ChatManager.SendMessage listenerCallback, Context context) {
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
            SendResponse sendResponse = parseBody(SendResponse.class, body);

            if (sendResponse.getResponse() == 0) {
                makeToastError(body, context);
                return;
            }

            listenerCallback.callback();
        }
    }

    public interface GetMessages extends Listener {
        void callback(List<DialogMessage> messages);
    }

    public interface GetLastMessage extends Listener {
        void callback(DialogMessage message);
    }

    public interface SendMessage extends Listener {
        void callback();
    }
}
