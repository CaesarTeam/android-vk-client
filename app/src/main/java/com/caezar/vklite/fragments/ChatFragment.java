package com.caezar.vklite.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.caezar.vklite.ChatManager;
import com.caezar.vklite.ChooseMessageTypeListener;
import com.caezar.vklite.FragmentCallback;
import com.caezar.vklite.R;
import com.caezar.vklite.UserManager;
import com.caezar.vklite.adapters.ChatAdapter;
import com.caezar.vklite.Config;
import com.caezar.vklite.libs.ChatInstanceState;
import com.caezar.vklite.models.MessageAction;
import com.caezar.vklite.models.User;
import com.caezar.vklite.models.network.request.ChatRequest;
import com.caezar.vklite.models.DialogMessage;

import java.util.List;

import static com.caezar.vklite.fragments.DialogsFragment.CHAT_FRAGMENT_TAG;
import static com.caezar.vklite.fragments.DialogsFragment.PEER_ID;
import static com.caezar.vklite.fragments.DialogsFragment.TITLE;
import static com.caezar.vklite.fragments.ImageMessageFullScreenFragment.IMAGE_FULL_FRAGMENT_TAG;
import static com.caezar.vklite.fragments.MessageActionDialog.MESSAGE_ACTION_FRAGMENT_TAG;
import static com.caezar.vklite.libs.DialogsHelper.getChatIdFromPeerId;
import static com.caezar.vklite.libs.KeyBoard.hideKeyboard;
import static com.caezar.vklite.libs.KeyBoard.showKeyboard;

/**
 * Created by seva on 03.04.18 in 15:40.
 */

public class ChatFragment extends Fragment implements ChooseMessageTypeListener {
    public static final String PHOTO_URL = "photoUrl";
    public static final String DIALOG_MESSAGE = "dialogMessage";

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText editText;
    Button sendMessage;
    Button submitEditMessage;

    private int peer_id;
    private boolean isPrivateDialog;
    private boolean isChatRequest = true;

    @NonNull private final EditMessageListener editMessageListener = new EditMessageListener();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String title = "";
        if (getArguments() != null) {
            peer_id = getArguments().getInt(PEER_ID, 0);
            title = getArguments().getString(TITLE);

            isPrivateDialog = peer_id < Config.peerIdConstant;
        }

        TextView textView = view.findViewById(R.id.messageTitle);
        textView.setText(title);

        editText = view.findViewById(R.id.messageForm);

        sendMessage = view.findViewById(R.id.buttonSendMessage);
        submitEditMessage = view.findViewById(R.id.buttonSubmitEdit);

        sendMessage.setVisibility(View.VISIBLE);
        sendMessage.setOnClickListener(v -> {
            hideKeyboard(editText);
            final String message = editText.getText().toString();
            editText.getText().clear();
            ChatManager.getInstance().sendMessage(message, peer_id, new MessageSent(), getContext());
            recyclerView.scrollToPosition(0);
        });

        submitEditMessage.setVisibility(View.GONE);
        submitEditMessage.setOnClickListener(editMessageListener);

        recyclerView = view.findViewById(R.id.messagesList);
        adapter = new ChatAdapter(new ChatCallbacks(), getContext(), isPrivateDialog);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);


        if (ChatInstanceState.getInstance().getMessages() != null) {
            addMessagesToAdapterTop(ChatInstanceState.getInstance().getMessages());
        }

        if (ChatInstanceState.getInstance().getPhotoUsers() != null) {
            setAvatarsToAdapter(ChatInstanceState.getInstance().getPhotoUsers());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!isPrivateDialog && adapter.getPhotoUsersSize() == 0) {
            getParticipantsChat(getChatIdFromPeerId(peer_id));
        }

        if (adapter.getItemCount() == 0) {
            getChat(0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (adapter.getItemCount() != 0) {
            ChatInstanceState.getInstance().setMessages(adapter.getItems());
        }

        if (adapter.getPhotoUsersSize() != 0) {
            ChatInstanceState.getInstance().setPhotoUsers(adapter.getPhotoUsers());
        }
    }

    private void getParticipantsChat(int chatId) {
        UserManager.getInstance().getUsersChat(chatId, new GetUserIds(), getContext());
    }

    private void getChat(int offset) {
        if (isChatRequest) {
            isChatRequest = false;
            int defaultCount = new ChatRequest().getCount(); //todo: fix it
            ChatManager.getInstance().getChat(offset, peer_id, defaultCount, new GetMessages(), getContext());
        }
    }

    private void addMessageToAdapterEnd(DialogMessage dialogMessage) {
        getActivity().runOnUiThread(() -> adapter.addItemToEnd(dialogMessage));
    }

    private void addMessagesToAdapterTop(List<DialogMessage> items) {
        getActivity().runOnUiThread(() -> {
            adapter.addItemsToTop(items);
            if (adapter.getItemCount() == new ChatRequest().getCount()) {
                recyclerView.scrollToPosition(0);
            }
            isChatRequest = true;
        });
    }

    private void setAvatarsToAdapter(SparseArray<String> photoUsers) {
        getActivity().runOnUiThread(() -> adapter.setUsersAvatar(photoUsers));
    }

    @Override
    public void onFinishDialogMessageType(MessageAction messageAction, DialogMessage dialogMessage) {
        switch (messageAction) {
            case REPLY:
                break;
            case FORWARD:
                break;
            case PIN:
                break;
            case COPY:
                break;
            case EDIT:
                editMessage(dialogMessage.getBody(), dialogMessage.getId());
                break;
            case DELETE:
                break;
        }
    }

    private void editMessage(String message, int messageId) {
        showKeyboard(editText);
        sendMessage.setVisibility(View.GONE);
        submitEditMessage.setVisibility(View.VISIBLE);
        editText.setText(message);
        editText.requestFocus();
        editText.setSelection(editText.getText().length());
        editMessageListener.setMessageId(messageId);
    }

    private class GetMessages implements ChatManager.GetMessages {
        @Override
        public void callback(List<DialogMessage> messages) {
            addMessagesToAdapterTop(messages);
        }

        public GetMessages() {
        }
    }

    private class MessageSent implements ChatManager.MessageActionDone {
        @Override
        public void callback(int messageId) {
            ChatManager.getInstance().getChat(0, peer_id, 1, new GetLastMessage(), getContext());
        }

        public MessageSent() {
        }
    }

    private class MessageEdited implements ChatManager.MessageActionDone {
        @Override
        public void callback(int messageId) {
            //ChatManager.getInstance().getChat(offset, peer_id, 1, new GetMessageById(), getContext());
        }

        public MessageEdited() {
        }
    }

    private class GetLastMessage implements ChatManager.GetLastMessage {
        @Override
        public void callback(DialogMessage message) {
            addMessageToAdapterEnd(message);
        }

        public GetLastMessage() {
        }
    }

    private class GetUserIds implements UserManager.GetUserIds {
        @Override
        public void callback(int[] userIds) {
            UserManager.getInstance().getUsers(userIds, new GetUsers(), getContext());
        }

        public GetUserIds() {
        }
    }

    private class GetUsers implements UserManager.GetUsers {
        @Override
        public void callback(User[] users) {
            final SparseArray<String> photoUsers = new SparseArray<>();

            for (User user: users) {
                photoUsers.append(user.getId(), user.getPhoto_50());
            }

            setAvatarsToAdapter(photoUsers);
        }

        public GetUsers() {

        }
    }

    public class ChatCallbacks implements FragmentCallback {
        public ChatCallbacks() {

        }

        public void getMoreMessages(int offset) {
            getChat(offset);
        }

        public void createFragmentFullSizeImageMessage(String photoUrl) {
            Bundle bundle = new Bundle();
            bundle.putString(PHOTO_URL, photoUrl);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

            ImageMessageFullScreenFragment imageMessageFullScreenFragment = new ImageMessageFullScreenFragment();
            imageMessageFullScreenFragment.setArguments(bundle);
            transaction.replace(R.id.mainContainer, imageMessageFullScreenFragment, IMAGE_FULL_FRAGMENT_TAG);
            transaction.addToBackStack(CHAT_FRAGMENT_TAG);

            transaction.commit();
        }

        public void createFragmentDialogMessageType(DialogMessage dialogMessage) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(DIALOG_MESSAGE, dialogMessage);
            MessageActionDialog messageActionDialog = new MessageActionDialog();
            messageActionDialog.setTargetFragment(ChatFragment.this, 0);
            messageActionDialog.setArguments(bundle);
            messageActionDialog.show(getActivity().getSupportFragmentManager(), MESSAGE_ACTION_FRAGMENT_TAG);
        }
    }

    public class EditMessageListener implements View.OnClickListener {
        int messageId;

        public EditMessageListener() {
        }

        @Override
        public void onClick(View v) {
            final String message = editText.getText().toString();
            editText.getText().clear();
            ChatManager.getInstance().editMessage(message, peer_id, messageId, new MessageEdited(), getContext());
            hideKeyboard(editText);
        }


        public void setMessageId(int messageId) {
            this.messageId = messageId;
        }
    }
}

