package com.caezar.vklite.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.caezar.vklite.Config;
import com.caezar.vklite.MainActivity;
import com.caezar.vklite.managers.DialogManager;
import com.caezar.vklite.FragmentCallbacks;
import com.caezar.vklite.R;
import com.caezar.vklite.managers.UserManager;
import com.caezar.vklite.adapters.DialogsAdapter;

import java.util.List;

import com.caezar.vklite.instanceState.ChatInstanceState;
import com.caezar.vklite.instanceState.DialogsInstanceState;
import com.caezar.vklite.models.network.DialogItem;
import com.caezar.vklite.models.network.User;
import com.caezar.vklite.models.network.response.PollingMessageNewEdit;

import static com.caezar.vklite.MainActivity.DIALOG_FRAGMENT_TAG;
import static com.caezar.vklite.fragments.ChatFragment.BROADCAST_EDIT_MESSAGE;
import static com.caezar.vklite.fragments.ChatFragment.BROADCAST_NEW_MESSAGE;
import static com.caezar.vklite.fragments.ChatFragment.NEW_MESSAGE;
import static com.caezar.vklite.helpers.LongPollingHelper.constructDialogItemFromPollingMessagesNew;
import static com.caezar.vklite.libs.Db.insertDialogs;
import static com.caezar.vklite.helpers.DialogsHelper.addDataToDialogsList;
import static com.caezar.vklite.helpers.DialogsHelper.getUsersIdFromPrivateDialogs;
import static com.caezar.vklite.helpers.ToolbarHelper.hideToolbarBack;
import static com.caezar.vklite.helpers.ToolbarHelper.setToolbarTitle;
import static com.caezar.vklite.libs.Guava.findIndexDialog;

/**
 * Created by seva on 01.04.18 in 17:56.
 */

public class DialogsFragment extends Fragment {
    public static final String TITLE = "title";
    public static final String PEER_ID = "peer_id";
    public static final String CHAT_FRAGMENT_TAG = "chatFragmentTag";
    public static final String BROADCAST_CLOSE_CHAT = "broadcastCloseChat";

    private RecyclerView recyclerView;
    private DialogsAdapter adapter;
    private ProgressBar progressBar;
    private boolean requestDialogsFinish = true;
    private boolean refresh = true;

    private final BroadcastReceiver closeChatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int peerId = intent.getIntExtra(PEER_ID, 0);
            int position = findIndexDialog(adapter.getItems(), peerId);
            adapter.animateClosedChat(recyclerView.findViewHolderForAdapterPosition(position));
            setToolbarProperty();
        }
    };

    private final BroadcastReceiver newMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<PollingMessageNewEdit> newMessageList = intent.getParcelableArrayListExtra(NEW_MESSAGE);
            List<DialogItem> dialogItems = constructDialogItemFromPollingMessagesNew(newMessageList);
            adapter.changeItems(dialogItems);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity)getActivity()).setOnBackPressedListener(null);
        return inflater.inflate(R.layout.fragment_dialogs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.dialogProgressBar);

        recyclerView = view.findViewById(R.id.dialogsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DialogsAdapter(new DialogsCallbacks(), getContext());
        recyclerView.setAdapter(adapter);

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(getContext(), R.color.colorDialog));
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

        if (getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(closeChatReceiver, new IntentFilter(BROADCAST_CLOSE_CHAT));
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(newMessageReceiver, new IntentFilter(BROADCAST_NEW_MESSAGE));
        }

        setToolbarProperty();
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

        if (getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(closeChatReceiver);
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(newMessageReceiver);
        }
    }

    private void setToolbarProperty() {
        String title = getString(R.string.app_name);
        if (!Config.ONLINE_MODE) {
            title += getString(R.string.toolbar_title_offline);
        }
        setToolbarTitle(getActivity().findViewById(R.id.toolbar), title);
        hideToolbarBack(getActivity().findViewById(R.id.toolbar));
    }

    private void getDialogs(int offset) {
        if (requestDialogsFinish) {
            requestDialogsFinish = false;
            progressBar.setVisibility(View.VISIBLE);
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
        progressBar.setVisibility(View.GONE);
    }

    private void setDialogsFromListener(List<DialogItem> dialogs) {
        if (getActivity() != null && getContext() != null) {
            insertDialogs(dialogs);
            getActivity().runOnUiThread(() -> setDialogs(dialogs));
        }
    }

    private class GetDialogs implements DialogManager.GetDialogs {
        @Override
        public void callback(List<DialogItem> dialogs) {
            final int[] userIds = getUsersIdFromPrivateDialogs(dialogs);
            if (userIds.length == 0 || !Config.ONLINE_MODE) {
                setDialogsFromListener(dialogs);
            } else {
                UserManager.getInstance().getUsers(userIds, new GetUsers(dialogs), getContext());
            }
        }

        GetDialogs() {
        }

    }

    private class GetUsers implements UserManager.GetUsers {
        final List<DialogItem> dialogs;

        @Override
        public void callback(User[] users) {
            addDataToDialogsList(dialogs, users);
            setDialogsFromListener(dialogs);
        }

        GetUsers(List<DialogItem> dialogs) {
            this.dialogs = dialogs;
        }
    }

    public class DialogsCallbacks implements FragmentCallbacks {
        DialogsCallbacks() {

        }

        public void openChat(int peer_id, String title) {
            ChatInstanceState.getInstance().reset();
            DialogsInstanceState.getInstance().reset();

            Bundle bundle = new Bundle();
            bundle.putString(TITLE, title);
            bundle.putInt(PEER_ID, peer_id);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

            ChatFragment chatFragment = new ChatFragment();
            chatFragment.setArguments(bundle);
            transaction.add(R.id.mainContainer, chatFragment, CHAT_FRAGMENT_TAG);
            transaction.addToBackStack(DIALOG_FRAGMENT_TAG);

            transaction.commit();
        }

        public void getMoreDialogs(int offset) {
            getDialogs(offset);
        }

    }
}