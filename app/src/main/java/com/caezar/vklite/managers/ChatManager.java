package com.caezar.vklite.managers;

import android.content.Context;
import android.support.annotation.NonNull;

import com.caezar.vklite.Listener;
import com.caezar.vklite.libs.UrlBuilder;
import com.caezar.vklite.models.network.DialogMessage;
import com.caezar.vklite.models.network.MessageAction;
import com.caezar.vklite.models.network.request.ChatRequest;
import com.caezar.vklite.models.network.request.DeleteMessageRequest;
import com.caezar.vklite.models.network.request.EditMessageRequest;
import com.caezar.vklite.models.network.request.SendMessageRequest;
import com.caezar.vklite.models.network.response.ChatResponse;
import com.caezar.vklite.models.network.response.MessageActionResponse;

import java.util.Arrays;
import java.util.List;

import static com.caezar.vklite.Config.ONLINE_MODE;
import static com.caezar.vklite.helpers.ErrorHelper.createErrorInternetToast;
import static com.caezar.vklite.helpers.ErrorHelper.makeToastError;
import static com.caezar.vklite.libs.Jackson.parseBody;

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
            NetworkManager.getInstance().get(url, new OnMessageActionComplete(MessageAction.SEND, (MessageActionDone)listener, context));
        } else {
            // todo: save to store to send when online
        }
    }

    public void editMessage(String message, int peer_id, int message_id, Listener listener, Context context) {
        if (ONLINE_MODE) {
            final EditMessageRequest editMessageRequest = new EditMessageRequest();
            editMessageRequest.setMessage(message);
            editMessageRequest.setPeer_id(peer_id);
            editMessageRequest.setMessage_id(message_id);
            final String url = UrlBuilder.constructEditMessage(editMessageRequest);
            OnMessageActionComplete onMessageActionComplete = new OnMessageActionComplete(MessageAction.EDIT, (MessageActionDone)listener, context);
            onMessageActionComplete.setMessageId(message_id);
            NetworkManager.getInstance().get(url, onMessageActionComplete);
        } else {
            // todo: save to store to edit message
        }
    }

    public void deleteMessage(int message_id, Listener listener, Context context) {
        if (ONLINE_MODE) {
            final DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest();
            int messageIds[] = new int[1];
            messageIds[0] = message_id;
            deleteMessageRequest.setMessage_ids(messageIds);
            final String url = UrlBuilder.constructDeleteMessage(deleteMessageRequest);
            OnMessageActionComplete onMessageActionComplete = new OnMessageActionComplete(MessageAction.DELETE, (MessageActionDone)listener, context);
            onMessageActionComplete.setMessageId(message_id);
            NetworkManager.getInstance().get(url, onMessageActionComplete);
        } else {
            // todo: save to store to edit message
        }
    }

    public void getChat(int offset, int peer_id, int count, Listener listener, Context context) {
        if (ONLINE_MODE) {
            final ChatRequest chatRequest = new ChatRequest();
            chatRequest.setCount(count);
            chatRequest.setOffset(offset);
            chatRequest.setPeer_id(peer_id);
            final String url = UrlBuilder.constructGetChat(chatRequest);
            NetworkManager.getInstance().get(url, new OnGetMessagesChatComplete(listener, context));
        } else {

        }
    }

    public void getMessage(int peer_id, int messageId, Listener listener, Context context) {
        if (ONLINE_MODE) {
            final ChatRequest chatRequest = new ChatRequest();
            chatRequest.setCount(1);
            chatRequest.setPeer_id(peer_id);
            chatRequest.setStart_message_id(messageId);
            final String url = UrlBuilder.constructGetChat(chatRequest);
            NetworkManager.getInstance().get(url, new OnGetMessagesChatComplete(listener, context));
        } else {

        }
    }

    private class OnGetMessagesChatComplete implements NetworkManager.OnRequestCompleteListener {
        private final Listener listenerCallback;
        private final Context context;

        OnGetMessagesChatComplete(Listener listenerCallback, Context context) {
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
                ((GetMessages)listenerCallback).getSizeChat(chatResponse.getResponse().getCount());
                ((GetMessages)listenerCallback).callback(messages);
            } else if (listenerCallback instanceof GetLastMessage) {
                ((GetLastMessage)listenerCallback).callback(messages.get(0));
            } else if (listenerCallback instanceof GetMessageById) {
                ((GetMessageById)listenerCallback).callback(messages.get(0));
            }
        }
    }

    private class OnMessageActionComplete implements NetworkManager.OnRequestCompleteListener {
        private final MessageActionDone listenerCallback;
        private final Context context;
        private final MessageAction messageAction;
        private int messageId;

        OnMessageActionComplete(MessageAction messageAction, MessageActionDone listenerCallback, Context context) {
            this.messageAction = messageAction;
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
            //todo: do parsing error if parse make toast else everything is all right
            MessageActionResponse messageActionResponse = parseBody(MessageActionResponse.class, body);

            if (messageActionResponse == null || messageActionResponse.getResponse() == 0) {
                makeToastError(body, context);
                return;
            }

            switch (messageAction) {
                case EDIT:
                case DELETE:
                    listenerCallback.callback(messageId);
                    break;
                case SEND:
                    listenerCallback.callback(messageActionResponse.getResponse());
                    break;
            }
        }

        void setMessageId(int messageId) {
            this.messageId = messageId;
        }
    }

    public interface GetMessages extends Listener {
        void callback(List<DialogMessage> messages);

        void getSizeChat(int size);
    }

    public interface GetLastMessage extends Listener {
        void callback(DialogMessage message);
    }

    public interface GetMessageById extends Listener {
        void callback(DialogMessage message);
    }

    public interface MessageActionDone extends Listener {
        void callback(int messageId);
    }
}
