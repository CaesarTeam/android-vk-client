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
    private final NetworkManager.OnRequestCompleteListener listenerChats =
            new NetworkManager.OnRequestCompleteListener() {
                @Override
                public void onRequestComplete(final String body) {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            List<DialogMessage> items = buildItemList(body);
                            if (items != null) {
                                adapter.addDataToTop(items);
                            }
                        }
                    });
                }
            };

    private final NetworkManager.OnRequestCompleteListener listenerSend =
            new NetworkManager.OnRequestCompleteListener() {
                @Override
                public void onRequestComplete(final String body) {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            Log.d("Response", body);
                        }
                    });
                }
            };

    private int peer_id;
    private String title;
    private ChatAdapter adapter;
    private RecyclerView recyclerView;
    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            peer_id = b.getInt("peer_id");
            title = b.getString("title");
        }

        TextView textView = findViewById(R.id.messageTitle);
        textView.setText(title);

        editText = findViewById(R.id.EditTextName);
        button = findViewById(R.id.sendMessage);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();
                editText.getText().clear();
                editText.requestFocus();
                sendMessage(message);
                DialogMessage dialogMessage = new DialogMessage();
                dialogMessage.setFrom_id(MetaInfo.getMyselfId());
                dialogMessage.setBody(message);
                adapter.addDataToEnd(dialogMessage);
            }
        });


        recyclerView = findViewById(R.id.recyclerView2);
        adapter = new ChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        final SwipeRefreshLayout mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                int currentCountOfMessage = adapter.getItemCount();
                requestChat(currentCountOfMessage);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        requestChat(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void requestChat(int offset) {
        final ChatRequest chatRequest = new ChatRequest();
        chatRequest.setOffset(offset);
        chatRequest.setPeer_id(peer_id);
        final String url = urlBuilder.constructGetChat(chatRequest);
        NetworkManager.getInstance().get(url, listenerChats);
    }

    private void sendMessage(String message) {
        final SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setMessage(message);
        sendMessageRequest.setPeer_id(peer_id);
        final String url = urlBuilder.constructGetSend(sendMessageRequest);
        NetworkManager.getInstance().get(url, listenerSend);
    }

    public List<DialogMessage> buildItemList(String body) {
        Log.d("Response", body);

        List<DialogMessage> items = null;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        TypeReference<ChatResponse> mapType = new TypeReference<ChatResponse>() {};
        try {
            ChatResponse chatResponse = mapper.readValue(body, mapType);
            Collections.reverse(Arrays.asList(chatResponse.getResponse().getItems()));
            items = Arrays.asList(chatResponse.getResponse().getItems());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }
}
