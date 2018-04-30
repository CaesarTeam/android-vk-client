package com.caezar.vklite.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.caezar.vklite.ChatManager;
import com.caezar.vklite.R;
import com.caezar.vklite.UserManager;
import com.caezar.vklite.adapters.ChatAdapter;
import com.caezar.vklite.fragments.ImageMessageFullScreenFragment;
import com.caezar.vklite.Config;
import com.caezar.vklite.libs.ChatInstanceState;
import com.caezar.vklite.models.network.User;
import com.caezar.vklite.models.network.request.ChatRequest;
import com.caezar.vklite.models.network.DialogMessage;

import java.util.List;

import static com.caezar.vklite.activities.DialogsActivity.PEER_ID;
import static com.caezar.vklite.activities.DialogsActivity.TITLE;
import static com.caezar.vklite.libs.DialogsHelper.getChatIdFromPeerId;
import static com.caezar.vklite.libs.KeyBoard.hideKeyboard;

/**
 * Created by seva on 03.04.18 in 15:40.
 */

public class ChatActivity extends AppCompatActivity {
    public static final String PHOTO_URL = "photoUrl";

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText editText;

    private int peer_id;
    private boolean isPrivateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        peer_id = getIntent().getExtras().getInt(PEER_ID, 0);
        isPrivateDialog = peer_id < Config.peerIdConstant;

        TextView textView = findViewById(R.id.messageTitle);
        textView.setText(getIntent().getExtras().getString(TITLE));

        editText = findViewById(R.id.messageForm);

        Button button = findViewById(R.id.buttonSendMessage);
        button.setOnClickListener(v -> {
                final String message = editText.getText().toString();
                editText.getText().clear();
                ChatManager.getInstance().sendMessage(message, peer_id, new SendMessage(), this);
                recyclerView.scrollToPosition(0);
                hideKeyboard(editText);
        });

        recyclerView = findViewById(R.id.messagesList);
        adapter = new ChatAdapter(this, isPrivateDialog);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
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
    protected void onStart() {
        super.onStart();

        if (!isPrivateDialog && adapter.getPhotoUsersSize() == 0) {
            getParticipantsChat(getChatIdFromPeerId(peer_id));
        }

        if (adapter.getItemCount() == 0) {
            getChat(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (adapter.getItemCount() != 0) {
            ChatInstanceState.getInstance().setMessages(adapter.getItems());
        }

        if (adapter.getPhotoUsersSize() != 0) {
            ChatInstanceState.getInstance().setPhotoUsers(adapter.getPhotoUsers());
        }
    }

    public void getMessageCallback(int offset) {
        getChat(offset);
    }

    private void getParticipantsChat(int chatId) {
        UserManager.getInstance().getUsersChat(chatId, new GetUserIds(), this);
    }

    private void getChat(int offset) {
        int defaultCount = new ChatRequest().getCount(); //todo: fix it
        ChatManager.getInstance().getChat(offset, peer_id, defaultCount, new GetMessages(), this);
    }

    private void addMessageToAdapterEnd(DialogMessage dialogMessage) {
        runOnUiThread(() -> adapter.addItemToEnd(dialogMessage));
    }

    private void addMessagesToAdapterTop(List<DialogMessage> items) {
        runOnUiThread(() -> {
            adapter.addItemsToTop(items);
            if (adapter.getItemCount() == new ChatRequest().getCount()) {
                recyclerView.scrollToPosition(0);
            }
        });
    }

    private void setAvatarsToAdapter(SparseArray<String> photoUsers) {
        runOnUiThread(() -> adapter.setUsersAvatar(photoUsers));
    }

    public void createFragmentFullSizeImageMessage(String photoUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(PHOTO_URL, photoUrl);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        ImageMessageFullScreenFragment imageMessageFullScreenFragment = new ImageMessageFullScreenFragment();
        imageMessageFullScreenFragment.setArguments(bundle);
        transaction.replace(R.id.chatContainer, imageMessageFullScreenFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    private class GetMessages implements ChatManager.GetMessages {
        @Override
        public void callback(List<DialogMessage> messages) {
            addMessagesToAdapterTop(messages);
        }

        public GetMessages() {
        }
    }

    private class SendMessage implements ChatManager.SendMessage {
        @Override
        public void callback() {
            ChatManager.getInstance().getChat(0, peer_id, 1, new GetLastMessage(), ChatActivity.this);
        }

        public SendMessage() {
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
            UserManager.getInstance().getUsers(userIds, new GetUsers(), ChatActivity.this);
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
}

