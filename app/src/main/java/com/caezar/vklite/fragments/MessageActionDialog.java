package com.caezar.vklite.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.caezar.vklite.ChooseMessageTypeListener;
import com.caezar.vklite.R;
import com.caezar.vklite.models.DialogMessage;
import com.caezar.vklite.models.MessageAction;

import static com.caezar.vklite.fragments.ChatFragment.DIALOG_MESSAGE;

/**
 * Created by seva on 30.04.18 in 17:15.
 */

public class MessageActionDialog extends DialogFragment {
    public static final String MESSAGE_ACTION_FRAGMENT_TAG = "messageActionFragmentTag";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_dialog_message_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DialogMessage dialogMessage = getArguments().getParcelable(DIALOG_MESSAGE);

        Button reply = view.findViewById(R.id.messageActionReply);
        Button forward = view.findViewById(R.id.messageActionForward);
        Button pin = view.findViewById(R.id.messageActionPin);
        Button copy = view.findViewById(R.id.messageActionCopy);
        Button edit = view.findViewById(R.id.messageActionEdit);
        Button delete = view.findViewById(R.id.messageActionDelete);

        reply.setOnClickListener(v -> {
            this.dismiss();
            ((ChooseMessageTypeListener)getTargetFragment()).onFinishDialogMessageType(MessageAction.REPLY, dialogMessage);
        });

        forward.setOnClickListener(v -> {
            this.dismiss();
            ((ChooseMessageTypeListener)getTargetFragment()).onFinishDialogMessageType(MessageAction.FORWARD, dialogMessage);
        });

        pin.setOnClickListener(v -> {
            this.dismiss();
            ((ChooseMessageTypeListener)getTargetFragment()).onFinishDialogMessageType(MessageAction.PIN, dialogMessage);
        });

        copy.setOnClickListener(v -> {
            this.dismiss();
            ((ChooseMessageTypeListener)getTargetFragment()).onFinishDialogMessageType(MessageAction.COPY, dialogMessage);
        });

        edit.setOnClickListener(v -> {
            this.dismiss();
            ((ChooseMessageTypeListener)getTargetFragment()).onFinishDialogMessageType(MessageAction.EDIT, dialogMessage);
        });

        delete.setOnClickListener(v -> {
            this.dismiss();
            ((ChooseMessageTypeListener)getTargetFragment()).onFinishDialogMessageType(MessageAction.DELETE, dialogMessage);
        });
    }
}