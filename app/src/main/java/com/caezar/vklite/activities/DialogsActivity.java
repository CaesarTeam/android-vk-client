package com.caezar.vklite.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.caezar.vklite.R;
import com.caezar.vklite.adapters.DialogsAdapter;
import com.caezar.vklite.network.NetworkManager;
import com.caezar.vklite.network.models.DialogsRequest;
import com.caezar.vklite.network.models.DialogsResponse;
import com.caezar.vklite.network.urlBuilder;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.caezar.vklite.network.models.DialogsResponse.Response.DialogItem;

/**
 * Created by seva on 01.04.18 in 17:56.
 */
public class DialogsActivity extends AppCompatActivity {
    private final NetworkManager.OnRequestCompleteListener listener =
            new NetworkManager.OnRequestCompleteListener() {
                @Override
                public void onRequestComplete(final String body) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<DialogItem> items = buildItemList(body);
                            adapter = new DialogsAdapter(items);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
            };

    private final static DialogsRequest dialogsRequest = new DialogsRequest();
    private DialogsAdapter adapter;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final String url = urlBuilder.constructGetDialogs(dialogsRequest);
        NetworkManager.getInstance().get(url, listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public List<DialogItem> buildItemList(String body) {
        Log.d("buildItemList", body);
        List<DialogItem> items = null;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeReference<DialogsResponse> mapType = new TypeReference<DialogsResponse>() {};
        try {
            DialogsResponse dialogsResponse = mapper.readValue(body, mapType);
            Log.d("DialogsResponse", dialogsResponse.toString());
            items = Arrays.asList(dialogsResponse.getResponse().getItems());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return items;
    }
}