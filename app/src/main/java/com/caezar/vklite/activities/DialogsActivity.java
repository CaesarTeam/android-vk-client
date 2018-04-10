package com.caezar.vklite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.caezar.vklite.R;
import com.caezar.vklite.adapters.DialogsAdapter;
import com.caezar.vklite.libs.ConfiguredObjectMapper;
import com.caezar.vklite.network.NetworkManager;
import com.caezar.vklite.network.models.DialogsRequest;
import com.caezar.vklite.network.models.DialogsResponse;
import com.caezar.vklite.network.models.UsersByIdRequest;
import com.caezar.vklite.network.models.UsersByIdResponse;
import com.caezar.vklite.network.urlBuilder;

import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.caezar.vklite.network.models.DialogsResponse.Response.DialogItem;

import static com.caezar.vklite.ErrorHandle.errorParse;

/**
 * Created by seva on 01.04.18 in 17:56.
 */
// todo: margin between dialogs
public class DialogsActivity extends AppCompatActivity {
    public static final String PHOTO_PARTICIPANTS = "photoParticipants";
    public static final String TITLE = "title";
    public static final String PEER_ID = "peer_id";

    private DialogsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<DialogItem> items = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

        RecyclerView recyclerView = findViewById(R.id.dialogsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DialogsAdapter(this, items);
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

    public void openChat(int peer_id, String title, int[] photoParticipants) {
        Intent intent = new Intent(DialogsActivity.this, ChatActivity.class);
        intent.putExtra(PEER_ID, peer_id);
        intent.putExtra(TITLE, title);
        intent.putExtra(PHOTO_PARTICIPANTS, photoParticipants);
        startActivity(intent);
    }

    private void getDialogs() {
        final String url = urlBuilder.constructGetDialogs(new DialogsRequest());
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
        public void onResponse(final String body) {
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
            TypeReference<DialogsResponse> mapType = new TypeReference<DialogsResponse>() {};
            DialogsResponse dialogsResponse = new DialogsResponse();

            try {
                dialogsResponse = ConfiguredObjectMapper.getInstance().readValue(body, mapType);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (dialogsResponse.getResponse() == null) {
                final int stringRes = errorParse(body);
                if (stringRes != -1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DialogsActivity.this, stringRes, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return;
            }

            items = Arrays.asList(dialogsResponse.getResponse().getItems());
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

        private void requestGetInfoAboutUsers(int[] userIds) {
            final UsersByIdRequest usersByIdRequest = new UsersByIdRequest();
            usersByIdRequest.setUser_ids(userIds);

            final String url = urlBuilder.constructGetUsersInfo(usersByIdRequest);
            NetworkManager.getInstance().get(url, this);
        }

        private void addInfoAboutUsersToList(final String body) {
            TypeReference<UsersByIdResponse> mapType = new TypeReference<UsersByIdResponse>() {};
            UsersByIdResponse usersByIdResponse = new UsersByIdResponse();

            try {
                usersByIdResponse = ConfiguredObjectMapper.getInstance().readValue(body, mapType);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (usersByIdResponse.getResponse() == null) {
                final int stringRes = errorParse(body);
                if (stringRes != -1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DialogsActivity.this, stringRes, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return;
            }

            for (DialogItem item : items) {
                if (item.getMessage().getTitle().equals("")) {
                    int userId = item.getMessage().getUser_id();

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