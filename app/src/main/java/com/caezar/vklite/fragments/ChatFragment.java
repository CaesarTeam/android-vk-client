package com.caezar.vklite.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.caezar.vklite.LinearLayoutManagerWithSmoothScroller;
import com.caezar.vklite.MainActivity;
import com.caezar.vklite.OnBackPressedListener;
import com.caezar.vklite.managers.ChatManager;
import com.caezar.vklite.ChooseMessageTypeListener;
import com.caezar.vklite.FragmentCallbacks;
import com.caezar.vklite.R;
import com.caezar.vklite.managers.UserManager;
import com.caezar.vklite.adapters.ChatAdapter;
import com.caezar.vklite.Config;
import com.caezar.vklite.instanceState.ChatInstanceState;
import com.caezar.vklite.models.network.Message;
import com.caezar.vklite.models.network.MessageAction;
import com.caezar.vklite.models.network.User;
import com.caezar.vklite.models.network.DialogMessage;
import com.caezar.vklite.models.network.response.PollingMessageBase;
import com.caezar.vklite.models.network.response.PollingMessageNewEdit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;
import static com.caezar.vklite.Config.countItemsToRequestChat;
import static com.caezar.vklite.Config.getApplicationDownloadDir;
import static com.caezar.vklite.fragments.DialogsFragment.BROADCAST_CLOSE_CHAT;
import static com.caezar.vklite.fragments.DialogsFragment.CHAT_FRAGMENT_TAG;
import static com.caezar.vklite.fragments.DialogsFragment.PEER_ID;
import static com.caezar.vklite.fragments.DialogsFragment.TITLE;
import static com.caezar.vklite.fragments.ImageMessageFullScreenFragment.IMAGE_FULL_FRAGMENT_TAG;
import static com.caezar.vklite.fragments.MessageActionDialog.MESSAGE_ACTION_FRAGMENT_TAG;
import static com.caezar.vklite.helpers.ChatHelper.swapButtonsVisibility;
import static com.caezar.vklite.helpers.DialogsHelper.getChatIdFromPeerId;
import static com.caezar.vklite.helpers.LongPollingHelper.getMessageIdsForDeletedMessages;
import static com.caezar.vklite.helpers.LongPollingHelper.removePollingMessagesEditFromAnotherChat;
import static com.caezar.vklite.helpers.LongPollingHelper.removeUnnecessaryPollingMessagesNew;
import static com.caezar.vklite.helpers.LongPollingHelper.constructDialogMessageFromPollingMessagesNew;
import static com.caezar.vklite.libs.KeyBoard.copyToClipBoard;
import static com.caezar.vklite.libs.KeyBoard.hideKeyboard;
import static com.caezar.vklite.libs.KeyBoard.showKeyboard;
import static com.caezar.vklite.helpers.ToolbarHelper.setToolbarTitle;
import static com.caezar.vklite.helpers.ToolbarHelper.showToolbarBack;
import static com.caezar.vklite.managers.DownloadFilesManager.downloadFileFromUrl;

/**
 * Created by seva on 03.04.18 in 15:40.
 */

public class ChatFragment extends Fragment implements ChooseMessageTypeListener {
    public static final String PHOTO_URL = "photoUrl";
    public static final String DIALOG_MESSAGE = "dialogMessage";
    public static final String IS_MYSELF_MESSAGE = "isMyselfMessage";
    public static final String BROADCAST_NEW_MESSAGE = "broadcastNewMessage";
    public static final String BROADCAST_EDIT_MESSAGE = "broadcastEditMessage";
    public static final String BROADCAST_SET_FLAGS_MESSAGE = "broadcastSetFlagsMessage";
    public static final String NEW_MESSAGE = "newMessage";
    public static final String EDIT_MESSAGE = "editMessage";
    public static final String SET_FLAGS_MESSAGE = "setFlagsMessage";

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText editText;
    private ProgressBar progressBar;
    private Button sendMessage;
    private Button submitEditMessage;

    private int peer_id;
    private boolean isPrivateDialog;
    private boolean isChatRequest = true;

    @NonNull private final EditMessageListener editMessageListener = new EditMessageListener();

    private final BroadcastReceiver newMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<PollingMessageNewEdit> newMessageList = intent.getParcelableArrayListExtra(NEW_MESSAGE);
            List<Message> messages = new ArrayList<>(adapter.getItems());
            removeUnnecessaryPollingMessagesNew(newMessageList, messages, peer_id);
            List<DialogMessage> dialogMessages = constructDialogMessageFromPollingMessagesNew(newMessageList);

            if (dialogMessages.size() > 0) {
                adapter.addItemsToEnd(dialogMessages);
            }
        }
    };

    private final BroadcastReceiver editMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<PollingMessageNewEdit> newMessageList = intent.getParcelableArrayListExtra(EDIT_MESSAGE);
            removePollingMessagesEditFromAnotherChat(newMessageList, peer_id);
            List<DialogMessage> dialogMessages = constructDialogMessageFromPollingMessagesNew(newMessageList);
            if (dialogMessages.size() > 0) {
                changeMessages(dialogMessages);
            }
        }
    };

    private final BroadcastReceiver setFlagsMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<PollingMessageBase> pollingMessageBases = intent.getParcelableArrayListExtra(SET_FLAGS_MESSAGE);
            removePollingMessagesEditFromAnotherChat(pollingMessageBases, peer_id);

            List<Integer> messageIds = getMessageIdsForDeletedMessages(pollingMessageBases);
            if (messageIds.size() > 0) {
                deleteMessage(messageIds);
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity)getActivity()).setOnBackPressedListener(new ChatOnBackPressedListener());
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.chatProgressBar);

        if (getArguments() != null) {
            peer_id = getArguments().getInt(PEER_ID, 0);
            isPrivateDialog = peer_id < Config.peerIdConstant;
            String title = getArguments().getString(TITLE);
            setToolbarTitle(getActivity().findViewById(R.id.toolbar), title);
            showToolbarBack(getActivity().findViewById(R.id.toolbar));
        }


        editText = view.findViewById(R.id.messageForm);

        sendMessage = view.findViewById(R.id.buttonSendMessage);
        submitEditMessage = view.findViewById(R.id.buttonSubmitEdit);

        sendMessage.setOnClickListener(v -> {
            hideKeyboard(editText);
            final String message = editText.getText().toString();
            editText.getText().clear();
            ChatManager.getInstance().sendMessage(message, peer_id, new MessageSent(), getContext());
            recyclerView.scrollToPosition(0);
        });

        submitEditMessage.setOnClickListener(editMessageListener);

        recyclerView = view.findViewById(R.id.messagesList);
        adapter = new ChatAdapter(new ChatCallbacks(), getContext(), isPrivateDialog);

        recyclerView.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getContext(), VERTICAL, true));
        recyclerView.setAdapter(adapter);


        if (ChatInstanceState.getInstance().getMessages() != null) {
            addMessagesToAdapterTop(ChatInstanceState.getInstance().getMessages());
        }

        if (ChatInstanceState.getInstance().getPhotoUsers() != null) {
            setAvatarsToAdapter(ChatInstanceState.getInstance().getPhotoUsers());
        }

        if (getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(newMessageReceiver, new IntentFilter(BROADCAST_NEW_MESSAGE));
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(editMessageReceiver, new IntentFilter(BROADCAST_EDIT_MESSAGE));
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(setFlagsMessageReceiver, new IntentFilter(BROADCAST_SET_FLAGS_MESSAGE));
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

        if (getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(newMessageReceiver);
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(editMessageReceiver);
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(setFlagsMessageReceiver);
        }
    }

    private final static int REQUEST_PERMISSIONS = 123;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                for (int i = 0; i < permissions.length; i++) {
                    if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[i])
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        // todo: call callback
                        break;
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void getParticipantsChat(int chatId) {
        UserManager.getInstance().getUsersChat(chatId, new GetUserIds(), getContext());
    }

    private void getChat(int offset) {
        if (isChatRequest) {
            progressBar.setVisibility(View.VISIBLE);
            isChatRequest = false;
            ChatManager.getInstance().getChat(offset, peer_id, countItemsToRequestChat, new GetMessages(), getContext());
        }
    }

    private void setChatSize(int size) {
        adapter.setCountMessagesChat(size);
    }

    private void changeMessages(List<DialogMessage> dialogMessages) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> adapter.changeItems(dialogMessages));
        }
    }

    private void deleteMessage(List<Integer> messageIds) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> adapter.deleteItem(messageIds));
        }
    }

    private void addMessagesToAdapterTop(List<DialogMessage> items) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                adapter.addItemsToTop(items);
                progressBar.setVisibility(View.GONE);
                isChatRequest = true;
            });
        }
    }

    private void setAvatarsToAdapter(SparseArray<String> photoUsers) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> adapter.setUsersAvatar(photoUsers));
        }
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
                copyToClipBoard(getContext(), dialogMessage.getBody());
                Toast.makeText(getContext(), R.string.copiedToClipBoard, Toast.LENGTH_SHORT).show();
                break;
            case EDIT:
                editMessage(dialogMessage.getBody(), dialogMessage.getId());
                break;
            case DELETE:
                ChatManager.getInstance().deleteMessage(dialogMessage.getId(), new MessageDeleted(), getContext());
                break;
        }
    }

    private void editMessage(String message, int messageId) {
        showKeyboard(editText);
        swapButtonsVisibility(sendMessage, submitEditMessage);
        editText.setText(message);
        editText.requestFocus();
        editText.setSelection(editText.getText().length());
        editMessageListener.setMessageId(messageId);
    }

    private class ChatOnBackPressedListener implements OnBackPressedListener {
        @Override
        public void doBack() {
            Intent intent = new Intent(BROADCAST_CLOSE_CHAT);
            intent.putExtra(PEER_ID, peer_id);
            if (getContext() != null) {
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            }
        }
    }

    private class GetMessages implements ChatManager.GetMessages {
        @Override
        public void callback(List<DialogMessage> messages) {
            addMessagesToAdapterTop(messages);
        }

        @Override
        public void getSizeChat(int size) {
            setChatSize(size);
        }

        GetMessages() {
        }
    }

    private class MessageSent implements ChatManager.MessageActionDone {
        @Override
        public void callback() {
//            markIncomingMessagesRead(adapter.getItems(), messageId);
        }

        MessageSent() {
        }
    }

    private class MessageEdited implements ChatManager.MessageActionDone {
        @Override
        public void callback() {
        }

        MessageEdited() {
        }
    }

    private class MessageDeleted implements ChatManager.MessageActionDone {
        @Override
        public void callback() {
        }

        MessageDeleted() {
        }
    }

    private class GetUserIds implements UserManager.GetUserIds {
        @Override
        public void callback(int[] userIds) {
            UserManager.getInstance().getUsers(userIds, new GetUsers(), getContext());
        }

        GetUserIds() {
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

        GetUsers() {

        }
    }

    public class ChatCallbacks implements FragmentCallbacks {
        ChatCallbacks() {

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
            transaction.add(R.id.mainContainer, imageMessageFullScreenFragment, IMAGE_FULL_FRAGMENT_TAG);
            transaction.addToBackStack(CHAT_FRAGMENT_TAG);

            transaction.commit();
        }

        public void createFragmentDialogMessageType(DialogMessage dialogMessage, boolean isMyselfMessage) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(DIALOG_MESSAGE, dialogMessage);
            bundle.putBoolean(IS_MYSELF_MESSAGE, isMyselfMessage);
            MessageActionDialog messageActionDialog = new MessageActionDialog();
            messageActionDialog.setTargetFragment(ChatFragment.this, 0);
            messageActionDialog.setArguments(bundle);
            messageActionDialog.show(getActivity().getSupportFragmentManager(), MESSAGE_ACTION_FRAGMENT_TAG);
        }

        public void downloadDocument(String url, String name) {
            final String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            if (ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                if (isExternalStorageWritable()) {
                    File vkLiteFolder = getApplicationDownloadDir();
                    if (vkLiteFolder.exists() && vkLiteFolder.isDirectory()) {
                        downloadFileFromUrl(url, vkLiteFolder.getPath() + "/" + name);
                    } else {
                        if (vkLiteFolder.mkdir()) {
                            downloadFileFromUrl(url, vkLiteFolder.getPath() + name);
                        } else {
                            Log.d("dir", vkLiteFolder.getPath() + " not created");
                        }
                    }
                }
            } else {
                requestPermissions(new String[]{permission}, REQUEST_PERMISSIONS);
            }
        }

        public void scrollToPosition(int position) {
            recyclerView.smoothScrollToPosition(position);
        }
    }

    class EditMessageListener implements View.OnClickListener {
        int messageId;

        EditMessageListener() {
        }

        @Override
        public void onClick(View v) {
            final String message = editText.getText().toString();
            editText.getText().clear();
            ChatManager.getInstance().editMessage(message, peer_id, messageId, new MessageEdited(), getContext());
            hideKeyboard(editText);
            swapButtonsVisibility(sendMessage, submitEditMessage);
        }


        void setMessageId(int messageId) {
            this.messageId = messageId;
        }
    }
}

