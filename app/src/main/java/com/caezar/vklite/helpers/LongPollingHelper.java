package com.caezar.vklite.helpers;

import com.caezar.vklite.Config;
import com.caezar.vklite.models.network.DialogMessage;
import com.caezar.vklite.models.network.Message;
import com.caezar.vklite.models.network.PollingMessageNewFlags;
import com.caezar.vklite.models.network.response.PollingNewMessage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.caezar.vklite.helpers.DialogsHelper.checkIsChat;
import static com.caezar.vklite.libs.Guava.findIndexMessage;

/**
 * Created by seva on 28.05.18 in 20:53.
 */

public class LongPollingHelper {
    public static String getLongPollingUrl() {
        StringBuilder url = new StringBuilder();
        url.append("https://");
        url.append(Config.getServer());
        url.append("?act=a_check&key=");
        url.append(Config.getKey());
        url.append("&ts=");
        url.append(Config.getTs());
        url.append("&wait=25&mode=10&version=3");
        return url.toString();
    }

    public static PollingNewMessage constructMessage(Object[] objects) {
        int messageId = (int) objects[1];
        int flags = (int) objects[2];
        int peerId = (int) objects[3];
        int timestamp = (int) objects[4];
        String message = (String) objects[5];

        LinkedHashMap linkedHashMap = (LinkedHashMap) objects[6];
        int fromId = 0;
        if (linkedHashMap.containsKey("from")) {
            fromId = Integer.parseInt((String) linkedHashMap.get("from"));
        }

        return new PollingNewMessage(messageId, flags, peerId, timestamp, message, fromId);
    }

    private static List<PollingMessageNewFlags> getFlagsPollingMessagesNew(int flag) {
        List<PollingMessageNewFlags> pollingMessageNewFlags = new ArrayList<>();
        PollingMessageNewFlags[] flags = PollingMessageNewFlags.values();
        for (int i = flags.length - 1; i >= 0; i--) {
            int flagValue = flags[i].getFlag();
            if (flag - flagValue >= 0) {
                flag -= flagValue;
                pollingMessageNewFlags.add(flags[i]);
            }
        }
        return pollingMessageNewFlags;
    }

    public static List<DialogMessage> transformDialogMessageFromPollingMessagesNew(List<PollingNewMessage> newMessageList) {
        List<DialogMessage> dialogMessages = new ArrayList<>();
        for (PollingNewMessage pollingNewMessage : newMessageList) {
            List<PollingMessageNewFlags> flagsPollingMessagesNew = getFlagsPollingMessagesNew(pollingNewMessage.getFlags());

            DialogMessage dialogMessage = new DialogMessage();
            dialogMessage.setId(pollingNewMessage.getMessageId());
            dialogMessage.setDate(pollingNewMessage.getTimestamp());
            dialogMessage.setBody(pollingNewMessage.getMessage());

            if (checkIsChat(pollingNewMessage.getPeerId())) {
                dialogMessage.setFrom_id(pollingNewMessage.getFromId());
            } else {
                if (flagsPollingMessagesNew.contains(PollingMessageNewFlags.OUTBOX)) {
                    dialogMessage.setFrom_id(Config.getMyselfId());
                } else {
                    dialogMessage.setFrom_id(pollingNewMessage.getPeerId());
                }
            }

            if (flagsPollingMessagesNew.contains(PollingMessageNewFlags.UNREAD)) {
                dialogMessage.setRead_state(false);
            } else {
                dialogMessage.setRead_state(true);
            }

            dialogMessages.add(dialogMessage);
        }
        return dialogMessages;
    }

    /**
     * remove duplicates and messages from another chat
     */
    public static void removeUnnecessaryPollingMessagesNew(List<PollingNewMessage> pollingNewMessages, List<Message> messages, int peer_id) {
        for (int i = 0; i < pollingNewMessages.size(); i++) {
            PollingNewMessage pollingNewMessage = pollingNewMessages.get(i);
            if (pollingNewMessage.getPeerId() != peer_id || findIndexMessage(messages, pollingNewMessage.getMessageId()) != -1) {
                pollingNewMessages.remove(i);
                i--;
            }
        }
    }
}
