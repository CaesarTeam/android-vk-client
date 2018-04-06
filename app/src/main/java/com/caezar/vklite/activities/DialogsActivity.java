package com.caezar.vklite.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
    private DialogsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<DialogItem> items = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DialogsAdapter(items);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getDialogs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void getDialogs() {
        final DialogsRequest dialogsRequest = new DialogsRequest();
        final String url = urlBuilder.constructGetDialogs(dialogsRequest);
        NetworkManager.getInstance().get(url, new OnGetDialogsComplete());
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeRefreshLayout.setRefreshing(false);
            getDialogs();
        }
    };

    private class OnGetDialogsComplete implements NetworkManager.OnRequestCompleteListener {
        private boolean dialogsComplete = false;

        public OnGetDialogsComplete() {
        }

        @Override
        public void onRequestComplete(final String body) {
            Log.d("Response", body);

            if (!dialogsComplete) {
                buildDialogsList(body);
                dialogsComplete = true;
                final int[] userIds = getUsersIdFromPrivateDialogs();
                requestGetInfoAboutUsers(userIds);

                return;
            }

            addInfoAboutUsersToList(body);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.changeItems(items);
                }
            });

        }

        private void buildDialogsList(final String body) {
            // todo: one mapper with right setting and import them from all project
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            TypeReference<DialogsResponse> mapType = new TypeReference<DialogsResponse>() {};
            try {
                DialogsResponse dialogsResponse = mapper.readValue(body, mapType);
                items = Arrays.asList(dialogsResponse.getResponse().getItems());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private int[] getUsersIdFromPrivateDialogs() {
            int[] userIds = new int[items.size()];
            int i = 0;
            for (DialogItem item : items) {
                if (item.getMessage().getTitle().equals("")) {
                    userIds[i] = item.getMessage().getUser_id();
                    i++;
                }
            }

            return userIds;
        }

        private void requestGetInfoAboutUsers(int[] userIds){
            final UsersByIdRequest usersByIdRequest = new UsersByIdRequest();
            usersByIdRequest.setUser_ids(userIds);

            final String url = urlBuilder.constructGetUsersInfo(usersByIdRequest);
            NetworkManager.getInstance().get(url, this);
        }

        private void addInfoAboutUsersToList(final String body) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            TypeReference<UsersByIdResponse> mapType = new TypeReference<UsersByIdResponse>() {};
            UsersByIdResponse usersByIdResponse = new UsersByIdResponse();
            try {
                usersByIdResponse = mapper.readValue(body, mapType);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (DialogItem item : items) {
                if (item.getMessage().getTitle().equals("")) {
                    int userId = item.getMessage().getUser_id();
                    UsersByIdResponse.Response response = new UsersByIdResponse.Response();

                    for (UsersByIdResponse.Response user : usersByIdResponse.getResponse()) {
                        if (user.getId() == userId) {
                            item.getMessage().setTitle(user.getFirst_name() + " " + user.getLast_name());
                            item.getMessage().setPhoto_50(user.getPhoto_50());
                            item.getMessage().setPhoto_100(user.getPhoto_100());
                            item.getMessage().setPhoto_200(user.getPhoto_200());
                            break;
                        }
                    }
                }
            }

        }
    }
}