package com.caezar.vklite.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.caezar.vklite.R;
import com.caezar.vklite.adapters.ChatAdapter;
import com.caezar.vklite.network.MetaInfo;
import com.caezar.vklite.network.NetworkManager;
import com.caezar.vklite.network.models.ChatRequest;
import com.caezar.vklite.network.models.ChatResponse;
import com.caezar.vklite.network.models.DialogMessage;
import com.caezar.vklite.network.models.SendMessageRequest;
import com.caezar.vklite.network.urlBuilder;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by seva on 03.04.18 in 15:40.
 */

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText editText;
    private LinearLayoutManager linearLayoutManager;
    // todo: local
    private SwipeRefreshLayout swipeRefreshLayout;

    private int peer_id;
    private int myselfId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle bundle = getIntent().getExtras();
        String title = "";
        if (bundle != null) {
            peer_id = bundle.getInt(getString(R.string.peer_id));
            title = bundle.getString(getString(R.string.title));
        }
        myselfId = MetaInfo.getMyselfId();

        TextView textView = findViewById(R.id.messageTitle);
        textView.setText(title);

        editText = findViewById(R.id.messageForm);

        Button button = findViewById(R.id.buttonSendMessage);
        button.setOnClickListener(onClickListener);

        recyclerView = findViewById(R.id.messagesList);
        adapter = new ChatAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

    }

    @Override
    protected void onStart() {
        super.onStart();

        getChat(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        adapter.addItemToEnd(dialogMessage);
    }

    private void addMessagesToAdaperTop(List<DialogMessage> items) {
        if (items != null) {
            adapter.addItemsToTop(items);
        }
    }

    public List<DialogMessage> buildMessageList(String body) {
        Log.d("Response", body);

        List<DialogMessage> messages = null;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        TypeReference<ChatResponse> mapType = new TypeReference<ChatResponse>() {};
        try {
            ChatResponse chatResponse = mapper.readValue(body, mapType);
            Collections.reverse(Arrays.asList(chatResponse.getResponse().getItems()));
            messages = Arrays.asList(chatResponse.getResponse().getItems());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return messages;
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            final String message = editText.getText().toString();
            editText.getText().clear();
            sendMessage(message);
            addMessageToAdapterEnd(message);
        }
    };

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeRefreshLayout.setRefreshing(false);
            int offset = adapter.getItemCount();
            getChat(offset);
        }
    };

    private class OnGetMessagesComplete implements NetworkManager.OnRequestCompleteListener {

        @Override
        public void onRequestComplete(final String body) {
            Log.d("Response", body);
            // todo: check that is doing not in main thread
            final List<DialogMessage> messages = buildMessageList(body);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addMessagesToAdaperTop(messages);

                    int itemCount = adapter.getItemCount();
                    if (itemCount == new ChatRequest().getCount()) {
                        recyclerView.scrollToPosition(itemCount - 1);
                    }
                }
            });
        }
    }

    private class OnSendMessageComplete implements NetworkManager.OnRequestCompleteListener {

        @Override
        public void onRequestComplete(final String body) {
            Log.d("Response", body);

        }
    }
}
