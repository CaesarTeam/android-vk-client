package com.caezar.vklite;

import com.caezar.vklite.models.network.DialogMessage;
import com.caezar.vklite.models.network.MessageAction;

/**
 * Created by seva on 30.04.18 in 18:46.
 */

public interface ChooseMessageTypeListener {
    void onFinishDialogMessageType(MessageAction messageAction, DialogMessage dialogMessage);
}
