package com.caezar.vklite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.caezar.vklite.R;
import com.caezar.vklite.adapters.DialogsAdapter;
import com.caezar.vklite.NetworkManager;
import com.caezar.vklite.models.request.DialogsRequest;
import com.caezar.vklite.models.response.DialogsResponse;
import com.caezar.vklite.models.request.UsersByIdRequest;
import com.caezar.vklite.models.response.UsersByIdResponse;
import com.caezar.vklite.libs.urlBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.caezar.vklite.models.DialogItem;

import static com.caezar.vklite.ErrorHandler.createErrorInternetFragment;
import static com.caezar.vklite.ErrorHandler.makeToastError;
import static com.caezar.vklite.libs.ParseResponse.parseBody;

/**
 * Created by seva on 01.04.18 in 17:56.
 */
// todo: margin between dialogs
public class DialogsActivity extends AppCompatActivity {
    public static final String PHOTO_PARTICIPANTS = "photoParticipants";
    public static final String TITLE = "title";
    public static final String PEER_ID = "peer_id";
    public static final String DIALOGS = "dialogs";

    private DialogsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<DialogItem> dialogs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

        if (savedInstanceState != null && savedInstanceState.getSerializable(DIALOGS) != null) {
            dialogs = savedInstanceState.getParcelableArrayList(DIALOGS);
        }

        RecyclerView recyclerView = findViewById(R.id.dialogsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DialogsAdapter(this, dialogs);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (dialogs != null) {
            outState.putParcelableArrayList(DIALOGS, new ArrayList<>(dialogs));
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (dialogs == null) {
            getDialogs();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        public void onError(String body) {
            createErrorInternetFragment(R.id.dialogsContainer, getSupportFragmentManager());
        }

        @Override
        public void onErrorCode(int code) {
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
                    adapter.changeItems(dialogs);
                }
            });

        }

        private void buildDialogsList(final String body) {
            DialogsResponse dialogsResponse = parseBody(DialogsResponse.class, body);

            if (dialogsResponse.getResponse() == null) {
                makeToastError(body, DialogsActivity.this);
                return;
            }

            dialogs = Arrays.asList(dialogsResponse.getResponse().getItems());
        }

        private int[] getUsersIdFromPrivateDialogs() {
            int[] userIds = new int[dialogs.size()];
            int i = 0;
            for (DialogItem item : dialogs) {
                if (TextUtils.isEmpty(item.getMessage().getTitle())) {
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
            UsersByIdResponse usersByIdResponse = parseBody(UsersByIdResponse.class, body);

            if (usersByIdResponse.getResponse() == null) {
                makeToastError(body, DialogsActivity.this);
                return;
            }

            for (DialogItem item : dialogs) {
                if (TextUtils.isEmpty(item.getMessage().getTitle())) {
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