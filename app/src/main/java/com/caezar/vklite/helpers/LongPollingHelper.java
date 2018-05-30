package com.caezar.vklite.helpers;

import com.caezar.vklite.Config;
import com.caezar.vklite.models.network.DialogMessage;
import com.caezar.vklite.models.network.Message;
import com.caezar.vklite.models.network.PollingMessageFlags;
import com.caezar.vklite.models.network.response.PollingMessageBase;
import com.caezar.vklite.models.network.response.PollingMessageNewEdit;

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

    public static PollingMessageNewEdit constructMessage(Object[] objects) {
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

        return new PollingMessageNewEdit(messageId, flags, peerId, timestamp, message, fromId);
    }

    public static PollingMessageBase constructSetFlagsMessage(Object[] objects) {
        int messageId = (int) objects[1];
        int flags = (int) objects[2];
        int peerId = (int) objects[3];
        return new PollingMessageBase(messageId, flags, peerId);
    }

    private static List<PollingMessageFlags> getFlagsPollingMessagesNew(int flag) {
        List<PollingMessageFlags> pollingMessageFlags = new ArrayList<>();
        PollingMessageFlags[] flags = PollingMessageFlags.values();
        for (int i = flags.length - 1; i >= 0; i--) {
            int flagValue = flags[i].getFlag();
            if (flag - flagValue >= 0) {
                flag -= flagValue;
                pollingMessageFlags.add(flags[i]);
            }
        }
        return pollingMessageFlags;
    }

    public static List<DialogMessage> transformDialogMessageFromPollingMessagesNew(List<PollingMessageNewEdit> newMessageList) {
        List<DialogMessage> dialogMessages = new ArrayList<>();
        for (PollingMessageNewEdit pollingMessageNewEdit: newMessageList) {
            List<PollingMessageFlags> flagsPollingMessagesNew = getFlagsPollingMessagesNew(pollingMessageNewEdit.getFlags());

            DialogMessage dialogMessage = new DialogMessage();
            dialogMessage.setId(pollingMessageNewEdit.getMessageId());
            dialogMessage.setDate(pollingMessageNewEdit.getTimestamp());
            dialogMessage.setBody(pollingMessageNewEdit.getMessage());

            if (checkIsChat(pollingMessageNewEdit.getPeerId())) {
                dialogMessage.setFrom_id(pollingMessageNewEdit.getFromId());
            } else {
                if (flagsPollingMessagesNew.contains(PollingMessageFlags.OUTBOX)) {
                    dialogMessage.setFrom_id(Config.getMyselfId());
                } else {
                    dialogMessage.setFrom_id(pollingMessageNewEdit.getPeerId());
                }
            }

            if (flagsPollingMessagesNew.contains(PollingMessageFlags.UNREAD)) {
                dialogMessage.setRead_state(false);
            } else {
                dialogMessage.setRead_state(true);
            }

            dialogMessages.add(dialogMessage);
        }
        return dialogMessages;
    }

    private static <T extends PollingMessageBase> List<Integer> getMessageIdsFromPollingMessageBase(List<T> messagesList) {
        List<Integer> messageIds = new ArrayList<>();
        for (T t: messagesList) {
            messageIds.add(t.getMessageId());
        }
        return messageIds;
    }

    public static <T extends PollingMessageBase> List<Integer> getMessageIdsForDeletedMessages(List<T> messagesList) {
        List<PollingMessageBase> candidatesToDelete = new ArrayList<>();
        for (PollingMessageBase pollingMessageBase: messagesList) {
            candidatesToDelete.add(pollingMessageBase.clone());
        }
        removePollingMessagesNonDeleted(candidatesToDelete);
        return getMessageIdsFromPollingMessageBase(candidatesToDelete);
    }

    /**
     * remove duplicates and messages from another chat
     */
    public static void removeUnnecessaryPollingMessagesNew(List<PollingMessageNewEdit> pollingMessagesNewEdit, List<Message> messages, int peer_id) {
        for (int i = 0; i < pollingMessagesNewEdit.size(); i++) {
            PollingMessageNewEdit pollingMessageNewEdit = pollingMessagesNewEdit.get(i);
            if (pollingMessageNewEdit.getPeerId() != peer_id || findIndexMessage(messages, pollingMessageNewEdit.getMessageId()) != -1) {
                pollingMessagesNewEdit.remove(i);
                i--;
            }
        }
    }

    public static <T extends PollingMessageBase> void removePollingMessagesEditFromAnotherChat(List<T> pollingMessageBases, int peer_id) {
        for (int i = 0; i < pollingMessageBases.size(); i++) {
            PollingMessageBase pollingMessageBase = pollingMessageBases.get(i);
            if (pollingMessageBase.getPeerId() != peer_id) {
                pollingMessageBases.remove(i);
                i--;
            }
        }
    }

    private static <T extends PollingMessageBase> void removePollingMessagesNonDeleted(List<T> pollingMessageBases) {
        for (int i = 0; i < pollingMessageBases.size(); i++) {
            PollingMessageBase pollingMessageBase = pollingMessageBases.get(i);
            List<PollingMessageFlags> flagsPollingMessagesNew = getFlagsPollingMessagesNew(pollingMessageBase.getFlags());
            if (!flagsPollingMessagesNew.contains(PollingMessageFlags.DELETED_FOR_ALL)) {
                pollingMessageBases.remove(i);
                i--;
            }
        }
    }
}
