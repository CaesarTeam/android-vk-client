package com.caezar.vklite.libs;

import android.content.Context;

import com.caezar.vklite.R;
import com.caezar.vklite.models.network.Attachments;
import com.caezar.vklite.models.network.DialogItem;
import com.caezar.vklite.models.network.DialogMessage;

import static com.caezar.vklite.Config.peerIdConstant;

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

    public static String getActionMessage(DialogMessage dialogMessage, Context context) {
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

    public static String getAttachmentsMessage(DialogMessage dialogMessage, Context context) {
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
}