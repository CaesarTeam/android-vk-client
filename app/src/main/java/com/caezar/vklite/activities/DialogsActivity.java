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
import com.caezar.vklite.network.models.UsersByIdRequest;
import com.caezar.vklite.network.models.UsersByIdResponse;
import com.caezar.vklite.network.urlBuilder;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.caezar.vklite.network.models.DialogsResponse.Response.DialogItem;

/**
 * Created by seva on 01.04.18 in 17:56.
 */
public class DialogsActivity extends AppCompatActivity {
    List<DialogItem> items = null;

    private final NetworkManager.OnRequestCompleteListener listenerDialogs =
            new NetworkManager.OnRequestCompleteListener() {
                @Override
                public void onRequestComplete(final String body) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buildItemList(body);
                            getUsersNameAndAvatar();
                        }
                    });
                }
            };

    private final NetworkManager.OnRequestCompleteListener listenerUsers =
            new NetworkManager.OnRequestCompleteListener() {
                @Override
                public void onRequestComplete(final String body) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<DialogItem> items = addInfoAboutUsersToItemList(body);
                            adapter = new DialogsAdapter(items);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
            };

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

        final DialogsRequest dialogsRequest = new DialogsRequest();
        final String url = urlBuilder.constructGetDialogs(dialogsRequest);
        NetworkManager.getInstance().get(url, listenerDialogs);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void getUsersNameAndAvatar() {
        int[] userIds = new int[items.size()];
        int index = 0;
        for (DialogItem item : items) {
            if (item.getMessage().getTitle().equals("")) {
                userIds[index] = item.getMessage().getUser_id();
                index++;
            }
        }

        final UsersByIdRequest usersByid = new UsersByIdRequest();
        usersByid.setUser_ids(userIds);
        final String url = urlBuilder.constructGetUsersInfo(usersByid);
        NetworkManager.getInstance().get(url, listenerUsers);
    }

    public void buildItemList(String body) {
        Log.d("buildItemList", body);

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
    }

    public List<DialogItem> addInfoAboutUsersToItemList(String body) {
        Log.d("buildItemList", body);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        TypeReference<UsersByIdResponse> mapType = new TypeReference<UsersByIdResponse>() {};
        UsersByIdResponse usersByIdResponse = new UsersByIdResponse();
        try {
            usersByIdResponse = mapper.readValue(body, mapType);
            Log.d("DialogsResponse", usersByIdResponse.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (DialogItem item : items) {
            if (item.getMessage().getTitle().equals("")) {
                int userId = item.getMessage().getUser_id();
                UsersByIdResponse.Response response = new UsersByIdResponse.Response();

                for (UsersByIdResponse.Response r : usersByIdResponse.getResponse()) {
                    if (r.getId() == userId) {
                        response = r;
                        break;
                    }
                }

                String title = response.getFirst_name() + " " + response.getLast_name();
                String photo_50 = response.getPhoto_50();
                String photo_100 = response.getPhoto_100();
                String photo_200 = response.getPhoto_200();

                item.getMessage().setTitle(title);
                item.getMessage().setPhoto_50(photo_50);
                item.getMessage().setPhoto_100(photo_100);
                item.getMessage().setPhoto_200(photo_200);
            }
        }

        return items;
    }

}