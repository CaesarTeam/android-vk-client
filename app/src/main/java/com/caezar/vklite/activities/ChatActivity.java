package com.caezar.vklite.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.caezar.vklite.R;
import com.caezar.vklite.adapters.ChatAdapter;
import com.caezar.vklite.adapters.DialogsAdapter;
import com.caezar.vklite.network.NetworkManager;
import com.caezar.vklite.network.models.ChatRequest;
import com.caezar.vklite.network.models.ChatResponse;
import com.caezar.vklite.network.models.DialogsRequest;
import com.caezar.vklite.network.models.DialogsResponse;
import com.caezar.vklite.network.models.UsersByIdRequest;
import com.caezar.vklite.network.models.UsersByIdResponse;
import com.caezar.vklite.network.modelsResponse.DialogMessage;
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
                            adapter = new ChatAdapter(items);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
            };

    private int peer_id;
    private String title;
    private ChatAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            peer_id = b.getInt("peer_id");
            title = b.getString("title");

        }

        recyclerView = findViewById(R.id.recyclerView2);
        TextView textView = findViewById(R.id.messageTitle);
        textView.setText(title);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final SwipeRefreshLayout mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("onRefresh", "message");
                mSwipeRefreshLayout.setRefreshing(false);

                requestChat(0);
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
