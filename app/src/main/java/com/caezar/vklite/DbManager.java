package com.caezar.vklite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.caezar.vklite.models.db.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by seva on 12.04.18 in 12:23.
 */

@SuppressLint("FieldCanBeLocal")
public class DbManager {
    private static final int VERSION = 1;

    @SuppressLint("StaticFieldLeak")
    private static final DbManager INSTANCE = new DbManager();

    private final String TABLE_DIALOGS = "Dialogs";
    private final String DIALOGS_COLUMN_TITLE = "title";
    private final String DIALOGS_COLUMN_MESSAGE = "message";
    private final String DIALOGS_COLUMN_IMAGE_URL = "imageUrl";
    private final String DIALOGS_COLUMN_PEER_ID = "peerId";

    private final String COLUMN_DATE = "date";

    public static DbManager getInstance(Context context) {
        INSTANCE.context = context.getApplicationContext();
        return INSTANCE;
    }

    private final Executor executor = Executors.newSingleThreadExecutor();

    private Context context;

    private SQLiteDatabase database;

    public <T extends BaseModel> void insert(final T model) {
        executor.execute(() -> insertInternal(model));
    }

    public void readAll(final ReadAllListener<String> listener) {
        executor.execute(() -> readAllInternal(listener));
    }

    public void clean() {
        executor.execute(() -> cleanInternal());
    }

    private void checkInitialized() {
        if (database != null) {
            return;
        }

        SQLiteOpenHelper helper = new SQLiteOpenHelper(context, "Vk.db", null, VERSION) {

            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE_DIALOGS + " ("
                        + DIALOGS_COLUMN_PEER_ID + " INTEGER PRIMARY KEY NOT NULL,"
                        + DIALOGS_COLUMN_TITLE + " TEXT NOT NULL, "
                        + DIALOGS_COLUMN_MESSAGE + " TEXT,"
                        + DIALOGS_COLUMN_IMAGE_URL + " TEXT,"
                        + COLUMN_DATE + " INTEGER NOT NULL);"
                );
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        };

        database = helper.getWritableDatabase();
    }

    private <T extends BaseModel> void insertInternal(T model) {
        checkInitialized();

        switch (model.getType()) {
            case DIALOG:
                insertDialog(model);
                break;
            case MESSAGE:
                break;
        }
    }

    private <T extends BaseModel> void insertDialog(T model) {
        // todo: to config
        int limit = 100;
        DialogModel dialogModel = (DialogModel) model;
        String title = dialogModel.getTitle();
        String message = dialogModel.getMessage();
        message = message.length() > limit ? message.substring(0, limit) : message;
        String imageUrl = dialogModel.getImageUrl();
        int date = dialogModel.getDate();
        int peerId = dialogModel.getPeerId();
        ContentValues values = new ContentValues();
        values.put(DIALOGS_COLUMN_PEER_ID, peerId);
        values.put(DIALOGS_COLUMN_TITLE, title);
        values.put(DIALOGS_COLUMN_MESSAGE, message);
        values.put(DIALOGS_COLUMN_IMAGE_URL, imageUrl);
        values.put(COLUMN_DATE, date);

        long rowId = database.insertWithOnConflict(TABLE_DIALOGS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (rowId == -1) {
            values.remove(DIALOGS_COLUMN_PEER_ID);
            database.update(TABLE_DIALOGS, values, DIALOGS_COLUMN_PEER_ID + "=" + peerId, null);
        }
    }

    private void readAllInternal(final ReadAllListener<String> listener) {
//        checkInitialized();
//
//        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
//        if (cursor == null) {
//            listener.onReadAll(Collections.<String>emptyList());
//            return;
//        }
//
//        final List<String> result = new ArrayList<>();
//        try {
//            while (cursor.moveToNext()) {
//                result.add(cursor.getString(cursor.getColumnIndex(TEXT_COLUMN)));
//            }
//        } finally {
//            cursor.close();
//        }
//        listener.onReadAll(result);
    }

    public void cleanInternal() {
        checkInitialized();

        database.execSQL("DELETE FROM " + TABLE_DIALOGS);
    }

    public interface ReadAllListener<T> {
        void onReadAll(final Collection<T> allItems);
    }
}
