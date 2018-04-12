package com.caezar.vklite.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.caezar.vklite.R;
import com.caezar.vklite.adapters.ChatAdapter;
import com.caezar.vklite.fragments.ImageMessageFullScreenFragment;
import com.caezar.vklite.libs.Time;
import com.caezar.vklite.Config;
import com.caezar.vklite.NetworkManager;
import com.caezar.vklite.models.network.request.ChatRequest;
import com.caezar.vklite.models.network.request.UsersChatRequest;
import com.caezar.vklite.models.network.response.ChatResponse;
import com.caezar.vklite.models.network.DialogMessage;
import com.caezar.vklite.models.network.request.SendMessageRequest;
import com.caezar.vklite.models.network.request.UsersByIdRequest;
import com.caezar.vklite.models.network.response.SendResponse;
import com.caezar.vklite.models.network.response.UsersByIdResponse;
import com.caezar.vklite.libs.urlBuilder;
import com.caezar.vklite.models.network.response.UsersChatResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText editText;

    private int peer_id;
    private int myselfId;
    private boolean isPrivateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        peer_id = getIntent().getIntExtra(PEER_ID, 0);
        isPrivateDialog = peer_id < Config.peerIdConstant;
        myselfId = Config.getMyselfId();

        TextView textView = findViewById(R.id.messageTitle);
        textView.setText(getIntent().getStringExtra(TITLE));

        editText = findViewById(R.id.messageForm);

        Button button = findViewById(R.id.buttonSendMessage);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String message = editText.getText().toString();
                editText.getText().clear();
                sendMessage(message);
                recyclerView.scrollToPosition(0);
                hideKeyboard(editText);
            }
        });

        recyclerView = findViewById(R.id.messagesList);
        adapter = new ChatAdapter(this, isPrivateDialog);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.chatSwipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                int offset = adapter.getItemCount();
                getChat(offset);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isPrivateDialog) {
            getParticipantsChat(getChatIdFromPeerId(peer_id));
        }
        getChat(0);
    }

    private void getParticipantsChat(int chatId) {
        UsersChatRequest usersChatRequest = new UsersChatRequest();
        usersChatRequest.setChat_id(chatId);
        final String url = urlBuilder.constructGetUsersChat(usersChatRequest);
        NetworkManager.getInstance().get(url, new OnGetUsersIdComplete());
    }

    private void getInfoAboutUsers(int[] userIds) {
        final UsersByIdRequest usersByIdRequest = new UsersByIdRequest();
        usersByIdRequest.setUser_ids(userIds);
        final String url = urlBuilder.constructGetUsersInfo(usersByIdRequest);
        NetworkManager.getInstance().get(url, new OnGetUsersAvatarsComplete());
    }

    private void getChat(int offset) {
        final ChatRequest chatRequest = new ChatRequest();
        chatRequest.setOffset(offset);
        chatRequest.setPeer_id(peer_id);
        final String url = urlBuilder.constructGetChat(chatRequest);
        NetworkManager.getInstance().get(url, new OnGetMessagesComplete());
    }

    private void sendMessage(final String message) {
        final SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setMessage(message);
        sendMessageRequest.setPeer_id(peer_id);
        final String url = urlBuilder.constructSendMessage(sendMessageRequest);
        NetworkManager.getInstance().get(url, new OnSendMessageComplete(message));
    }

    private void addMessageToAdapterEnd(final String message) {
        DialogMessage dialogMessage = new DialogMessage();
        dialogMessage.setFrom_id(myselfId);
        dialogMessage.setBody(message);
        dialogMessage.setDate(Time.currentDate());
        adapter.addItemToEnd(dialogMessage);
    }

    private void addMessagesToAdapterTop(List<DialogMessage> items) {
        adapter.addItemsToTop(items);
        if (adapter.getItemCount() == new ChatRequest().getCount()) {
            recyclerView.scrollToPosition(0);
        }
    }

    private void setAvatarsToAdapter(SparseArray<String> photoUsers) {
        adapter.setUsersAvatar(photoUsers);
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

            if (usersByIdResponse.getResponse() == null) {
                makeToastError(body, ChatActivity.this);
                return;
            }

            final SparseArray<String> photoUsers = new SparseArray<>();

            for (UsersByIdResponse.Response user: usersByIdResponse.getResponse()) {
                photoUsers.append(user.getId(), user.getPhoto_50());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setAvatarsToAdapter(photoUsers);
                }
            });

        }

        @Override
        public void onError(String body) {
            createErrorInternetToast(ChatActivity.this);
        }

        @Override
        public void onErrorCode(int code) {

        }
    }

    private class OnGetMessagesComplete implements NetworkManager.OnRequestCompleteListener {

        @Override
        public void onResponse(final String body) {
            ChatResponse chatResponse = parseBody(ChatResponse.class, body);

            if (chatResponse.getResponse() == null) {
                makeToastError(body, ChatActivity.this);
                return;
            }

            final List<DialogMessage> messages = Arrays.asList(chatResponse.getResponse().getItems());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addMessagesToAdapterTop(messages);
                }
            });
        }

        @Override
        public void onError(String body) {
            createErrorInternetToast(ChatActivity.this);
        }

        @Override
        public void onErrorCode(int code) {

        }
    }

    private class OnSendMessageComplete implements NetworkManager.OnRequestCompleteListener {
        private String message;

        OnSendMessageComplete(final String message) {
            this.message = message;
        }

        @Override
        public void onResponse(final String body) {
            SendResponse sendResponse = parseBody(SendResponse.class, body);

            if (sendResponse.getResponse() == 0) {
                makeToastError(body, ChatActivity.this);
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addMessageToAdapterEnd(message);
                }
            });
        }

        @Override
        public void onError(String body) {
            createErrorInternetToast(ChatActivity.this);
        }

        @Override
        public void onErrorCode(int code) {

        }
    }

}

