package com.caezar.vklite.libs;

import android.support.annotation.NonNull;

import com.caezar.vklite.DbManager;
import com.caezar.vklite.DialogManager;
import com.caezar.vklite.models.DialogMessage;
import com.caezar.vklite.models.db.BaseModel;
import com.caezar.vklite.models.db.DialogModel;
import com.caezar.vklite.models.DialogItem;

import java.util.ArrayList;
import java.util.List;

import static com.caezar.vklite.libs.DialogsHelper.checkIsChat;
import static com.caezar.vklite.libs.DialogsHelper.getChatIdFromPeerId;
import static com.caezar.vklite.libs.DialogsHelper.getPeerId;

/**
 * Created by seva on 12.04.18 in 12:55.
 */

public class Db {
    public static void insertDialogs(List<DialogItem> dialogs) {
        for (DialogItem dialogItem: dialogs) {
            DialogModel dialogModel = new DialogModel();
            dialogModel.setTitle(dialogItem.getMessage().getTitle());
            dialogModel.setMessage(dialogItem.getMessage().getBody());
            dialogModel.setImageUrl(dialogItem.getMessage().getPhoto_100());
            dialogModel.setPeerId(getPeerId(dialogItem));
            dialogModel.setDate(dialogItem.getMessage().getDate());
            DbManager.getInstance().insert(dialogModel);
        }
    }

    public static void getDialogs(DialogManager.GetDialogs listener, int limit, int offset) {
        DbManager.getInstance().select(BaseModel.Type.DIALOG, new GetDialogs(listener), limit, offset);
    }

    private static List<DialogItem> transformDialogsFromModel(List<DialogModel> dialogModels) {
        final List<DialogItem> dialogItems = new ArrayList<>();

        for (DialogModel dialogModel: dialogModels) {
            DialogItem dialogItem = new DialogItem();
            dialogItem.setMessage(new DialogMessage());
            dialogItem.getMessage().setTitle(dialogModel.getTitle());
            dialogItem.getMessage().setBody(dialogModel.getMessage());
            dialogItem.getMessage().setPhoto_100(dialogModel.getImageUrl());
            dialogItem.getMessage().setDate(dialogModel.getDate());
            int peerId = dialogModel.getPeerId();
            boolean isChat = checkIsChat(peerId);
            if (isChat) {
                dialogItem.getMessage().setChat_id(getChatIdFromPeerId(peerId));
            } else {
                dialogItem.getMessage().setChat_id(0);
                dialogItem.getMessage().setUser_id(peerId);
            }

            dialogItems.add(dialogItem);
        }

        return dialogItems;
    }

    public static class GetDialogs implements DbManager.DbListener {
        @NonNull private final DialogManager.GetDialogs listener;

        public GetDialogs(DialogManager.GetDialogs listener) {
            this.listener = listener;
        }

        @Override
        public <T extends BaseModel> void callback(List<T> models) {
            final List<DialogItem> dialogs = transformDialogsFromModel((List<DialogModel>) models);
            listener.callback(dialogs);
        }
    }
}
