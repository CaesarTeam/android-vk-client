package com.caezar.vklite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.caezar.vklite.DbManager;
import com.caezar.vklite.DialogManager;
import com.caezar.vklite.Listener;
import com.caezar.vklite.R;
import com.caezar.vklite.adapters.DialogsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.caezar.vklite.models.network.DialogItem;
import static com.caezar.vklite.libs.Db.insertDialogs;

/**
 * Created by seva on 01.04.18 in 17:56.
 */

public class DialogsActivity extends AppCompatActivity {
    public static final String TITLE = "title";
    public static final String PEER_ID = "peer_id";
    private static final String DIALOGS = "dialogs";

    private DialogsAdapter adapter;
    private boolean requestDialogsFinish = true;
    private boolean refresh = false;

    private DbManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

        manager = DbManager.getInstance(this);

        if (savedInstanceState != null && savedInstanceState.getSerializable(DIALOGS) != null) {
            requestDialogsFinish = false;
        }

        RecyclerView recyclerView = findViewById(R.id.dialogsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DialogsAdapter(this);
        recyclerView.setAdapter(adapter);

        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            refresh = true;
            getDialogs(0);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (adapter.getItems().size() == 0) {
            getDialogs(0);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.getSerializable(DIALOGS) != null) {
            List<DialogItem> dialogs = savedInstanceState.getParcelableArrayList(DIALOGS);
            setDialogs(dialogs);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (adapter.getItemCount() != 0) {
            outState.putParcelableArrayList(DIALOGS, new ArrayList<>(adapter.getItems()));
        }

        super.onSaveInstanceState(outState);
    }

    public void openChatCallback(int peer_id, String title) {
        Intent intent = new Intent(DialogsActivity.this, ChatActivity.class);
        intent.putExtra(PEER_ID, peer_id);
        intent.putExtra(TITLE, title);
        startActivity(intent);
    }

    public void getDialogsCallback(int offset) {
        getDialogs(offset);
    }

    private void getDialogs(int offset) {
        if (requestDialogsFinish) {
            requestDialogsFinish = false;
            DialogManager.getInstance().getDialogs(offset, new GetDialogs(), this);
        }
    }

    private void setDialogs(List<DialogItem> dialogs) {
        if (refresh) {
            adapter.setItems(dialogs);
            refresh = false;
        } else {
            adapter.addItemsToEnd(dialogs);
        }
        requestDialogsFinish = true;
    }

    private void setDialogsFromListener(List<DialogItem> dialogs) {
        insertDialogs(manager, dialogs);

        runOnUiThread(() -> setDialogs(dialogs));
    }

    public class GetDialogs implements Listener {

        public void callback(List<DialogItem> dialogs) {
            setDialogsFromListener(dialogs);
        }

        public GetDialogs() {
        }

    }
}