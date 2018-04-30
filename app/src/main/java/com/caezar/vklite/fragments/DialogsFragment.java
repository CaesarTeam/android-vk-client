package com.caezar.vklite.fragments;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caezar.vklite.DialogManager;
import com.caezar.vklite.FragmentCallback;
import com.caezar.vklite.R;
import com.caezar.vklite.UserManager;
import com.caezar.vklite.adapters.DialogsAdapter;

import java.util.List;

import com.caezar.vklite.libs.ChatInstanceState;
import com.caezar.vklite.libs.DialogsInstanceState;
import com.caezar.vklite.models.network.DialogItem;
import com.caezar.vklite.models.network.User;

import static com.caezar.vklite.libs.DialogsHelper.addDataToDialogsList;
import static com.caezar.vklite.libs.DialogsHelper.getUsersIdFromPrivateDialogs;

/**
 * Created by seva on 01.04.18 in 17:56.
 */

public class DialogsFragment extends Fragment {
    public static final String TITLE = "title";
    public static final String PEER_ID = "peer_id";

    private DialogsAdapter adapter;
    private boolean requestDialogsFinish = true;
    private boolean refresh = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_dialogs, container, false);

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectActivityLeaks().build());

        RecyclerView recyclerView = view.findViewById(R.id.dialogsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DialogsAdapter(new DialogsCallbacks(), getContext());
        recyclerView.setAdapter(adapter);

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (refresh) {
                refresh = false;
                swipeRefreshLayout.setRefreshing(false);
                getDialogs(0);
            }
        });

        if (DialogsInstanceState.getInstance().getDialogs() != null) {
            setDialogs(DialogsInstanceState.getInstance().getDialogs());
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (adapter.getItems().size() == 0) {
            getDialogs(0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (adapter.getItemCount() != 0) {
            DialogsInstanceState.getInstance().setDialogs(adapter.getItems());
        }
    }

    private void getDialogs(int offset) {
        if (requestDialogsFinish) {
            requestDialogsFinish = false;
            DialogManager.getInstance().getDialogs(offset, new GetDialogs(), getContext());
        }
    }

    private void setDialogs(List<DialogItem> dialogs) {
        if (!refresh) {
            adapter.setItems(dialogs);
            refresh = true;
        } else {
            adapter.addItemsToEnd(dialogs);
        }
        requestDialogsFinish = true;
    }

    private void setDialogsFromListener(List<DialogItem> dialogs) {
        getActivity().runOnUiThread(() -> setDialogs(dialogs));
    }

    private class GetDialogs implements DialogManager.GetDialogs {
        @Override
        public void callback(List<DialogItem> dialogs) {
            setDialogsFromListener(dialogs);
            final int[] userIds = getUsersIdFromPrivateDialogs(dialogs);
            UserManager.getInstance().getUsers(userIds, new GetUsers(dialogs), getContext());
        }

        public GetDialogs() {
        }

    }

    private class GetUsers implements UserManager.GetUsers {
        List<DialogItem> dialogs;

        @Override
        public void callback(User[] users) {
            addDataToDialogsList(dialogs, users);
            setDialogsFromListener(dialogs);
        }

        public GetUsers(List<DialogItem> dialogs) {
            this.dialogs = dialogs;
        }
    }

    public class DialogsCallbacks implements FragmentCallback {
        public DialogsCallbacks() {

        }

        public void openChat(int peer_id, String title) {
            ChatInstanceState.getInstance().reset();
            Bundle bundle = new Bundle();
            bundle.putString(TITLE, title);
            bundle.putInt(PEER_ID, peer_id);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

            ChatFragment chatFragment = new ChatFragment();
            chatFragment.setArguments(bundle);
            transaction.replace(R.id.mainContainer, chatFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        }

        public void getMoreDialogs(int offset) {
            getDialogs(offset);
        }

    }
}