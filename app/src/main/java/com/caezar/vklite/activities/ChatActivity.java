package com.caezar.vklite.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.caezar.vklite.R;
import com.caezar.vklite.adapters.ChatAdapter;
import com.caezar.vklite.fragments.ImageMessageFullScreen;
import com.caezar.vklite.libs.ConfiguredObjectMapper;
import com.caezar.vklite.libs.Time;
import com.caezar.vklite.network.MetaInfo;
import com.caezar.vklite.network.NetworkManager;
import com.caezar.vklite.network.models.ChatRequest;
import com.caezar.vklite.network.models.ChatResponse;
import com.caezar.vklite.network.models.DialogMessage;
import com.caezar.vklite.network.models.SendMessageRequest;
import com.caezar.vklite.network.models.UsersByIdRequest;
import com.caezar.vklite.network.models.UsersByIdResponse;
import com.caezar.vklite.network.urlBuilder;

import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.caezar.vklite.ErrorHandle.errorParse;
import static com.caezar.vklite.adapters.DialogsAdapter.IS_PRVATE_DIALOG;
import static com.caezar.vklite.adapters.DialogsAdapter.PEER_ID;
import static com.caezar.vklite.adapters.DialogsAdapter.PHOTO_PARTICIPANTS;
import static com.caezar.vklite.adapters.DialogsAdapter.TITLE;

/**
 * Created by seva on 03.04.18 in 15:40.
 */

public class ChatActivity extends AppCompatActivity {
    public static final String ACTION_OPEN_IMAGE_FULL_SIZE = "actionOpenImageFullSize";
    public static final String PHOTO_URL = "photoUrl";

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText editText;
    // todo: local
    private SwipeRefreshLayout swipeRefreshLayout;

    private int peer_id;
    private int myselfId;
    private int[] participantsId;
    private boolean isPrivateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        peer_id = getIntent().getIntExtra(PEER_ID, 0);
        String title = getIntent().getStringExtra(TITLE);
        isPrivateDialog = getIntent().getBooleanExtra(IS_PRVATE_DIALOG, true);
        participantsId = getIntent().getIntArrayExtra(PHOTO_PARTICIPANTS);

        myselfId = MetaInfo.getMyselfId();

        TextView textView = findViewById(R.id.messageTitle);
        textView.setText(title);

        editText = findViewById(R.id.messageForm);

        Button button = findViewById(R.id.buttonSendMessage);
        button.setOnClickListener(onClickListener);

        recyclerView = findViewById(R.id.messagesList);
        adapter = new ChatAdapter(this, isPrivateDialog);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.chatSwipeContainer);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        LocalBroadcastManager.getInstance(this).registerReceiver(openFullSizeImageBroadcastReceiver, new IntentFilter(ACTION_OPEN_IMAGE_FULL_SIZE));
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isPrivateDialog) {
            getInfoAboutUsers(participantsId);
        }
        getChat(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.unregisterReceiver(openFullSizeImageBroadcastReceiver);
    }

    private void getInfoAboutUsers(int[] userIds) {
        final UsersByIdRequest usersByIdRequest = new UsersByIdRequest();
        usersByIdRequest.setUser_ids(userIds);

        final String url = urlBuilder.constructGetUsersInfo(usersByIdRequest);
        NetworkManager.getInstance().get(url, new OnGetUsersInfoComplete());
    }

    private void getChat(int offset) {
        final ChatRequest chatRequest = new ChatRequest();
        chatRequest.setOffset(offset);
        chatRequest.setPeer_id(peer_id);
        final String url = urlBuilder.constructGetChat(chatRequest);
        NetworkManager.getInstance().get(url, new OnGetMessagesComplete());
    }

    private void sendMessage(String message) {
        final SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setMessage(message);
        sendMessageRequest.setPeer_id(peer_id);
        final String url = urlBuilder.constructGetSend(sendMessageRequest);
        NetworkManager.getInstance().get(url, new OnSendMessageComplete());
    }

    private void addMessageToAdapterEnd(final String message) {
        DialogMessage dialogMessage = new DialogMessage();
        dialogMessage.setFrom_id(myselfId);
        dialogMessage.setBody(message);
        dialogMessage.setDate(Time.currentDate());
        adapter.addItemToEnd(dialogMessage);
    }

    private void addMessagesToAdapterTop(List<DialogMessage> items) {
        if (items != null) {
            adapter.addItemsToTop(items);
        }
    }

    private final BroadcastReceiver openFullSizeImageBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = new Bundle();
            bundle.putString(PHOTO_URL, intent.getExtras().getString(PHOTO_URL));

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            ImageMessageFullScreen imageMessageFullScreen = new ImageMessageFullScreen();
            imageMessageFullScreen.setArguments(bundle);
            transaction.replace(R.id.chatContainer, imageMessageFullScreen);
            transaction.addToBackStack(null);

            transaction.commit();
        }
    };

    private final View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            final String message = editText.getText().toString();
            editText.getText().clear();
            sendMessage(message);
            addMessageToAdapterEnd(message);
            recyclerView.scrollToPosition(0);
        }
    };

    private final SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeRefreshLayout.setRefreshing(false);
            int offset = adapter.getItemCount();
            getChat(offset);
        }
    };

    private class OnGetMessagesComplete implements NetworkManager.OnRequestCompleteListener {

        @Override
        public void onResponse(final String body) {
            Log.d("Response", body);
            // todo: check that is doing not in main thread
            final List<DialogMessage> messages = buildMessageList(body);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addMessagesToAdapterTop(messages);

                    int itemCount = adapter.getItemCount();
                    if (itemCount == new ChatRequest().getCount()) {
                        recyclerView.scrollToPosition(0);
                    }
                }
            });
        }

        private List<DialogMessage> buildMessageList(String body) {
            TypeReference<ChatResponse> mapType = new TypeReference<ChatResponse>() {};
            ChatResponse chatResponse = new ChatResponse();

            try {
                chatResponse = ConfiguredObjectMapper.getInstance().readValue(body, mapType);
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<DialogMessage> messages = null;

            if (chatResponse.getResponse() == null) {
                final int stringRes = errorParse(body);
                if (stringRes != -1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChatActivity.this, stringRes, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return messages;
            }

            messages = Arrays.asList(chatResponse.getResponse().getItems());

            return messages;
        }
    }

    private class OnSendMessageComplete implements NetworkManager.OnRequestCompleteListener {

        @Override
        public void onResponse(final String body) {
            Log.d("Response", body);

        }
    }

    private class OnGetUsersInfoComplete implements NetworkManager.OnRequestCompleteListener {

        public OnGetUsersInfoComplete() {
        }

        @Override
        public void onResponse(final String body) {
            Log.d("Response", body);

            final Map<Integer, String> photoUsers = parseResponse(body);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.setUsersAvatar(photoUsers);
                }
            });

        }

        private Map<Integer, String> parseResponse(final String body) {
            TypeReference<UsersByIdResponse> mapType = new TypeReference<UsersByIdResponse>() {};
            UsersByIdResponse usersByIdResponse = new UsersByIdResponse();

            try {
                usersByIdResponse = ConfiguredObjectMapper.getInstance().readValue(body, mapType);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //todo: do something with hashMap
            Map<Integer, String> photoUsers = new HashMap<>();

            if (usersByIdResponse.getResponse() == null) {
                final int stringRes = errorParse(body);
                if (stringRes != -1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChatActivity.this, stringRes, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return photoUsers;
            }


            for (UsersByIdResponse.Response user: usersByIdResponse.getResponse()) {
                photoUsers.put(user.getId(), user.getPhoto_50());
            }

            return photoUsers;
        }
    }

}

