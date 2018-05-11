package com.caezar.vklite.helpers;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.caezar.vklite.R;
import com.caezar.vklite.models.network.Attachments;
import com.caezar.vklite.models.network.DialogItem;
import com.caezar.vklite.models.network.DialogMessage;
import com.caezar.vklite.models.network.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.caezar.vklite.Config.peerIdConstant;
import static com.caezar.vklite.libs.Guava.findUser;
import static com.caezar.vklite.libs.Guava.getPrivateDialogs;
import static com.caezar.vklite.libs.Guava.integerListToIntArray;

/**
 * Created by seva on 12.04.18 in 13:38.
 */

public class DialogsHelper {
    public static int getPeerId(DialogItem item) {
        final int chatId = item.getMessage().getChat_id();
        return chatId == 0 ? item.getMessage().getUser_id() : peerIdConstant + chatId;
    }

    public static int getChatIdFromPeerId(int peerId) {
        return peerId - peerIdConstant;
    }

    public static boolean checkIsChat(int peerId) {
        return peerId > peerIdConstant;
    }

    private static String getActionMessage(DialogMessage dialogMessage, Context context) {
        DialogMessage.Action action = dialogMessage.getAction();
        if (action != null) {
            switch (action) {
                case CHAT_CREATE:
                    return context.getString(R.string.messageTypeChatCreate);
                case CHAT_KICK_USER:
                    return context.getString(R.string.messageTypeKickUser);
                case CHAT_INVITE_USER:
                    return context.getString(R.string.messageTypeInviteUser);
                case CHAT_PIN_MESSAGE:
                    return context.getString(R.string.messageTypePinMessage);
                case CHAT_PHOTO_REMOVE:
                    return context.getString(R.string.messageTypeChatPhotoRemove);
                case CHAT_PHOTO_UPDATE:
                    return context.getString(R.string.messageTypeChatPhotoUpdate);
                case CHAT_TITLE_UPDATE:
                    return context.getString(R.string.messageTypeTitleUpdate);
                case CHAT_UNPIN_MESSAGE:
                    return context.getString(R.string.messageTypeUnpinMessage);
                case CHAT_INVITE_USER_BY_LINK:
                    return context.getString(R.string.messageTypeInviteUserByLink);
                default:
                    break;
            }
        }

        return null;
    }

    private static String getAttachmentsMessage(DialogMessage dialogMessage, Context context) {
        Attachments[] attachments = dialogMessage.getAttachments();
        if (attachments != null && attachments[0].getType() != null) {
            switch (attachments[0].getType()) {
                case STICKER:
                    return context.getString(R.string.messageTypeSticker);
                case PHOTO:
                    return context.getString(R.string.messageTypePhoto);
                case DOC:
                    return context.getString(R.string.messageTypeDoc);
                case GIFT:
                    return context.getString(R.string.messageTypeGift);
                case LINK:
                    return context.getString(R.string.messageTypeLink);
                case WALL:
                    return context.getString(R.string.messageTypeWall);
                case WALL_REPLY:
                    return context.getString(R.string.messageTypeWallReply);
                case AUDIO:
                    return context.getString(R.string.messageTypeAudio);
                case VIDEO:
                    return context.getString(R.string.messageTypeVideo);
                case MARKET:
                    return context.getString(R.string.messageTypeMarket);
                case MARKET_ALBUM:
                    return context.getString(R.string.messageTypeMarketAlbum);
                default:
                    break;
            }
        }

        return null;
    }

    private static String getDialogMessage(DialogMessage dialogMessage, Context context) {
        String attachmentTypeMessage = getAttachmentsMessage(dialogMessage, context);
        if (attachmentTypeMessage != null) {
            return attachmentTypeMessage;
        }

        String actionTypeMessage = getActionMessage(dialogMessage, context);
        if (actionTypeMessage != null) {
            return actionTypeMessage;
        }

        if (dialogMessage.getFwd_messages() != null) {
            return context.getString(R.string.messageTypeForwardMessage);
        }

        return dialogMessage.getBody();
    }

    public static boolean isTextMessage(DialogMessage dialogMessage) {
        return !TextUtils.isEmpty(dialogMessage.getBody());
    }

    public static int[] getUsersIdFromPrivateDialogs(List<DialogItem> dialogs) {
        Collection<DialogItem> privateDialogs = getPrivateDialogs(dialogs);

        List<Integer> userIds = new ArrayList<>(privateDialogs.size());
        for (DialogItem item: privateDialogs) {
            userIds.add(item.getMessage().getUser_id());
        }

        return integerListToIntArray(userIds);
    }

    public static void addDataToDialogsList(List<DialogItem> dialogs, User[] users) {
        Collection<DialogItem> privateDialogs = getPrivateDialogs(dialogs);
        List<User> usersList = Arrays.asList(users);

        for (DialogItem item: privateDialogs) {
            final int userId = item.getMessage().getUser_id();
            User user = findUser(usersList, userId);
            item.getMessage().setTitle(user.getFirst_name() + " " + user.getLast_name());
            item.getMessage().setPhoto_50(user.getPhoto_50());
            item.getMessage().setPhoto_100(user.getPhoto_100());
            item.getMessage().setPhoto_200(user.getPhoto_200());
            item.setOnline(user.isOnline());
        }
    }

    public static void setAvatarDialogOnline(RoundedImageView avatar, Context context) {
        avatar.setBorderWidth(9.f);
        avatar.setBorderColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        avatar.setPadding(5, 5, 5, 5);
    }

    public static void unsetAvatarDialogOnline(RoundedImageView avatar) {
        avatar.setBorderWidth(0.f);
        avatar.clearColorFilter();
        avatar.setPadding(0, 0, 0, 0);
    }

    public static void setReadState(TextView readState) {
        readState.setVisibility(View.VISIBLE);
    }

    public static void unsetReadState(TextView readState) {
        readState.setVisibility(View.INVISIBLE);
    }

    public static void setUnreadCount(TextView unreadCount, String countUnreadMessagesCount) {
        unreadCount.setText(countUnreadMessagesCount);
        unreadCount.setVisibility(View.VISIBLE);
    }

    public static void unsetUnreadCount(TextView unreadCount) {
        unreadCount.setVisibility(View.INVISIBLE);
    }

    public static void setDialogMessageText(DialogMessage dialogMessage, TextView message, Context context) {
        String body = getDialogMessage(dialogMessage, context);
        message.setText(body);
        message.setTextColor(ContextCompat.getColor(context, R.color.colorDialogMessageDefault));
    }

    public static void setDialogMessageNotText(DialogMessage dialogMessage, TextView message, Context context) {
        String body = getDialogMessage(dialogMessage, context);
        message.setText(body);
        message.setTextColor(ContextCompat.getColor(context, R.color.colorDialogMessageNotText));
    }
}