package com.caezar.vklite.libs;

import com.caezar.vklite.DbManager;
import com.caezar.vklite.models.db.DialogModel;
import com.caezar.vklite.models.network.DialogItem;

import java.util.List;

import static com.caezar.vklite.libs.DialogsHelper.getPeerId;

/**
 * Created by seva on 12.04.18 in 12:55.
 */

public class Db {
// todo: DbManager manager in this class
    public static void insertDialogs(DbManager manager, List<DialogItem> dialogs) {
        for (DialogItem dialogItem: dialogs) {
            DialogModel dialogModel = new DialogModel();
            dialogModel.setTitle(dialogItem.getMessage().getTitle());
            dialogModel.setMessage(dialogItem.getMessage().getBody());
            dialogModel.setImageUrl(dialogItem.getMessage().getPhoto_100());
            dialogModel.setPeerId(getPeerId(dialogItem));
            dialogModel.setDate(dialogItem.getMessage().getDate());
            manager.insert(dialogModel);
        }
    }
}
