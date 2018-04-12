package com.caezar.vklite.libs;

import com.caezar.vklite.models.network.DialogItem;

import static com.caezar.vklite.Config.peerIdConstant;

/**
 * Created by seva on 12.04.18 in 13:38.
 */

public class DialogsHelper {
    public static int getPeerId(DialogItem item) {
        final int chatId = item.getMessage().getChat_id();
        return  chatId == 0 ? item.getMessage().getUser_id() : peerIdConstant + chatId;
    }
}
