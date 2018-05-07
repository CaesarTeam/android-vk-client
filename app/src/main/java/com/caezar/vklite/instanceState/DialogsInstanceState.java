package com.caezar.vklite.instanceState;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.caezar.vklite.models.network.DialogItem;

import java.util.List;


/**
 * Created by seva on 30.04.18 in 13:33.
 */

public class DialogsInstanceState {
    private DialogsInstanceState() {
    }

    @NonNull
    private final static DialogsInstanceState INSTANCE = new DialogsInstanceState();

    public static DialogsInstanceState getInstance() {
        return INSTANCE;
    }

    @Nullable
    private List<DialogItem> dialogs = null;

    public void reset() {
        dialogs = null;
    }

    @Nullable
    public List<DialogItem> getDialogs() {
        return dialogs;
    }

    public void setDialogs(@Nullable List<DialogItem> dialogs) {
        this.dialogs = dialogs;
    }
}
