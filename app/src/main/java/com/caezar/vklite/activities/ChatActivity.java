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
import com.caezar.vklite.adapters.ChatAdapter;
import com.caezar.vklite.fragments.ImageMessageFullScreenFragment;
import com.caezar.vklite.Config;
import com.caezar.vklite.NetworkManager;
import com.caezar.vklite.models.network.User;
import com.caezar.vklite.models.network.request.ChatRequest;
import com.caezar.vklite.models.network.request.UsersChatRequest;
import com.caezar.vklite.models.network.DialogMessage;
import com.caezar.vklite.models.network.request.UsersByIdRequest;
import com.caezar.vklite.models.network.response.UsersByIdResponse;
import com.caezar.vklite.libs.UrlBuilder;
import com.caezar.vklite.models.network.response.UsersChatResponse;

import java.util.ArrayList;
import java.util.List;

import static com.caezar.vklite.ErrorHandler.createErrorInternetToast;
import static com.caezar.vklite.ErrorHandler.makeToastError;
import static com.caezar.vklite.activities.DialogsActivity.PEER_ID;
import static com.caezar.vklite.activities.DialogsActivity.TITLE;
import static com.caezar.vklite.libs.DialogsHelper.getChatIdFromPeerId;
import static com.caezar.vklite.libs.KeyBoard.hideKeyboard;
import static com.caezar.vklite.libs.ParseResponse.parseBody;

/**
 * Created by seva on 03.04.18 in 15:40.
 */

public class ChatActivity extends AppCompatActivity {
    public static final String PHOTO_URL = "photoUrl";
    private static final String MESSAGES = "messages";
    private static final String USERS_ID = "usersId";
    private static final String AVATARS_URL = "avatarsUrl";

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText editText;

    private int peer_id;
    private boolean isPrivateDialog;
    private boolean requestChatFinish = true;
    private boolean requestAvatarsFinish = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (savedInstanceState != null) {
            if (savedInstanceState.getSerializable(MESSAGES) != null) {
                requestChatFinish = false;
            }

            if (savedInstanceState.getSerializable(USERS_ID) != null) {
                requestAvatarsFinish = false;
            }
        }

        peer_id = getIntent().getIntExtra(PEER_ID, 0);
        isPrivateDialog = peer_id < Config.peerIdConstant;

        TextView textView = findViewById(R.id.messageTitle);
        textView.setText(getIntent().getStringExtra(TITLE));

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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.getSerializable(MESSAGES) != null) {
            List<DialogMessage> messages = savedInstanceState.getParcelableArrayList(MESSAGES);
            addMessagesToAdapterTop(messages);
        }

        if (savedInstanceState.getSerializable(USERS_ID) != null) {
            // todo: do something with this crazy
            int[] usersId = savedInstanceState.getIntArray(USERS_ID);
            ArrayList<String> avatarUrl = savedInstanceState.getStringArrayList(AVATARS_URL);
            SparseArray<String> photoUsers = new SparseArray<>();
            if (usersId != null) {
                for (int i = 0; i < usersId.length; i++) {
                    if (avatarUrl != null) {
                        photoUsers.append(usersId[i], avatarUrl.get(i));
                    }
                }
            }
            setAvatarsToAdapter(photoUsers);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (adapter.getItemCount() != 0) {
            outState.putParcelableArrayList(MESSAGES, new ArrayList<>(adapter.getItems()));
        }

        if (adapter.getPhotoUsersSize() != 0) {
            SparseArray<String> photoUsers = adapter.getPhotoUsers();
            int[] usersId = new int[photoUsers.size()];
            ArrayList<String> avatarUrl = new ArrayList<>();
            for (int i = 0; i < photoUsers.size(); i++) {
                final int key = photoUsers.keyAt(i);
                usersId[i] = (key);
                avatarUrl.add(photoUsers.get(key));
            }
            outState.putIntArray(USERS_ID, usersId);
            outState.putStringArrayList(AVATARS_URL, avatarUrl);
        }

        super.onSaveInstanceState(outState);
    }

    public void getMessageCallback(int offset) {
        getChat(offset);
    }

    private void getParticipantsChat(int chatId) {
        if (requestAvatarsFinish) {
            requestAvatarsFinish = false;
            UsersChatRequest usersChatRequest = new UsersChatRequest();
            usersChatRequest.setChat_id(chatId);
            final String url = UrlBuilder.constructGetUsersChat(usersChatRequest);
            NetworkManager.getInstance().get(url, new OnGetUsersIdComplete());
        }
    }

    private void getInfoAboutUsers(int[] userIds) {
        final UsersByIdRequest usersByIdRequest = new UsersByIdRequest();
        usersByIdRequest.setUser_ids(userIds);
        final String url = UrlBuilder.constructGetUsersInfo(usersByIdRequest);
        NetworkManager.getInstance().get(url, new OnGetUsersAvatarsComplete());
    }

    private void getChat(int offset) {
        if (requestChatFinish) {
            requestChatFinish = false;
            int defaultCount = new ChatRequest().getCount(); //todo: fix it
            ChatManager.getInstance().getChat(offset, peer_id, defaultCount, new GetMessages(), this);
        }
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
            requestChatFinish = true;
        });
    }

    private void setAvatarsToAdapter(SparseArray<String> photoUsers) {
        adapter.setUsersAvatar(photoUsers);
        requestAvatarsFinish = true;
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

    private class OnGetUsersIdComplete implements NetworkManager.OnRequestCompleteListener {

        public OnGetUsersIdComplete() {
        }

        @Override
        public void onResponse(final String body) {
            UsersChatResponse usersChatResponse = parseBody(UsersChatResponse.class, body);

            if (usersChatResponse.getResponse() == null) {
                makeToastError(body, ChatActivity.this);
                return;
            }

            final int[] usersId = usersChatResponse.getResponse().getUsers();
            getInfoAboutUsers(usersId);
        }

        @Override
        public void onError(String body) {
            createErrorInternetToast(ChatActivity.this);
        }

        @Override
        public void onErrorCode(int code) {
        }
    }

    private class OnGetUsersAvatarsComplete implements NetworkManager.OnRequestCompleteListener {

        public OnGetUsersAvatarsComplete() {
        }

        @Override
        public void onResponse(final String body) {
            UsersByIdResponse usersByIdResponse = parseBody(UsersByIdResponse.class, body);

            if (usersByIdResponse.getUsers() == null) {
                makeToastError(body, ChatActivity.this);
                return;
            }

            final SparseArray<String> photoUsers = new SparseArray<>();

            for (User user: usersByIdResponse.getUsers()) {
                photoUsers.append(user.getId(), user.getPhoto_50());
            }

            runOnUiThread(() -> setAvatarsToAdapter(photoUsers));

        }

        @Override
        public void onError(String body) {
            createErrorInternetToast(ChatActivity.this);
        }

        @Override
        public void onErrorCode(int code) {

        }
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
}

