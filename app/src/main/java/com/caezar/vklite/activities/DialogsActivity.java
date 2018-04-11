package com.caezar.vklite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
import java.util.Collection;
import java.util.List;

import com.caezar.vklite.models.DialogItem;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;

import static com.caezar.vklite.ErrorHandler.createErrorInternetToast;
import static com.caezar.vklite.ErrorHandler.makeToastError;
import static com.caezar.vklite.libs.ParseResponse.parseBody;
import static com.caezar.vklite.libs.Predicates.isEmptyTitle;
import static com.caezar.vklite.libs.Predicates.isPositiveUserId;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

        List<DialogItem> dialogs = null;
        if (savedInstanceState != null && savedInstanceState.getSerializable(DIALOGS) != null) {
            dialogs = savedInstanceState.getParcelableArrayList(DIALOGS);
        }

        RecyclerView recyclerView = findViewById(R.id.dialogsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DialogsAdapter(this, dialogs);
        recyclerView.setAdapter(adapter);

        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getDialogs();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (adapter.getItems() == null) {
            getDialogs();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (adapter.getItems() != null) {
            outState.putParcelableArrayList(DIALOGS, new ArrayList<>(adapter.getItems()));
        }

        super.onSaveInstanceState(outState);
    }

    public void openChatCallback(int peer_id, String title, int[] photoParticipants) {
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

    private void setDialogs(List<DialogItem> dialogs) {
        adapter.setItems(dialogs);
    }

    private class OnGetDialogsComplete implements NetworkManager.OnRequestCompleteListener {
        public OnGetDialogsComplete() {
        }

        @Override
        public void onError(String body) {
            createErrorInternetToast(DialogsActivity.this);
        }

        @Override
        public void onErrorCode(int code) {
        }

        @Override
        public void onResponse(final String body) {
            DialogsResponse dialogsResponse = parseBody(DialogsResponse.class, body);

            if (dialogsResponse.getResponse() == null) {
                makeToastError(body, DialogsActivity.this);
                return;
            }

            final List<DialogItem> dialogs = Arrays.asList(dialogsResponse.getResponse().getItems());

            final int[] userIds = getUsersIdFromPrivateDialogs(dialogs);
            if (userIds.length > 0) {
                requestGetUsers(userIds, dialogs);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setDialogs(dialogs);
                    }
                });
            }
        }

        private int[] getUsersIdFromPrivateDialogs(List<DialogItem> dialogs) {
            Collection<DialogItem> privateDialogs = Collections2.filter(dialogs, Predicates.and(isEmptyTitle, isPositiveUserId));

            List<Integer> userIds = new ArrayList<>(privateDialogs.size());
            for (DialogItem item: privateDialogs) {
                userIds.add(item.getMessage().getUser_id());
            }

            return Ints.toArray(userIds);
        }

        private void requestGetUsers(int[] userIds, List<DialogItem> dialogs) {
            final UsersByIdRequest usersByIdRequest = new UsersByIdRequest();
            usersByIdRequest.setUser_ids(userIds);
            final String url = urlBuilder.constructGetUsersInfo(usersByIdRequest);
            NetworkManager.getInstance().get(url, new OnGetUsersComplete(dialogs));
        }
    }

    private class OnGetUsersComplete implements NetworkManager.OnRequestCompleteListener {
        private List<DialogItem> dialogs;

        public OnGetUsersComplete(List<DialogItem> dialogs) {
            this.dialogs = dialogs;
        }

        @Override
        public void onError(String body) {
            createErrorInternetToast(DialogsActivity.this);
        }

        @Override
        public void onErrorCode(int code) {
        }

        @Override
        public void onResponse(final String body) {
            UsersByIdResponse usersByIdResponse = parseBody(UsersByIdResponse.class, body);

            if (usersByIdResponse.getResponse() == null) {
                makeToastError(body, DialogsActivity.this);
                return;
            }

            dialogs = addDataToDialogsList(dialogs, usersByIdResponse);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setDialogs(dialogs);
                }
            });
        }

        private List<DialogItem> addDataToDialogsList(List<DialogItem> dialogs, UsersByIdResponse usersByIdResponse) {
            Collection<DialogItem> privateDialogs = Collections2.filter(dialogs, Predicates.and(isEmptyTitle, isPositiveUserId));
            List<UsersByIdResponse.Response> users = Arrays.asList(usersByIdResponse.getResponse());

            for (DialogItem item: privateDialogs) {
                final int userId = item.getMessage().getUser_id();

                UsersByIdResponse.Response user = Iterables.find(users, new Predicate<UsersByIdResponse.Response> () {
                    public boolean apply(@NonNull UsersByIdResponse.Response user) {
                        return user.getId() == userId;
                    }
                });

                item.getMessage().setTitle(user.getFirst_name() + " " + user.getLast_name());
                item.getMessage().setPhoto_50(user.getPhoto_50());
                item.getMessage().setPhoto_100(user.getPhoto_100());
                item.getMessage().setPhoto_200(user.getPhoto_200());
            }

            return dialogs;
        }
    }
}