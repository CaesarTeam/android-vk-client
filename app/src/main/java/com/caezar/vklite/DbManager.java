package com.caezar.vklite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.caezar.vklite.models.db.*;

import java.util.ArrayList;
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

    private final String DIALOGS_TABLE = "Dialogs";
    private final String DIALOGS_COLUMN_TITLE = "title";
    private final String DIALOGS_COLUMN_MESSAGE = "message";
    private final String DIALOGS_COLUMN_IMAGE_URL = "imageUrl";
    private final String DIALOGS_COLUMN_PEER_ID = "peerId";

    private final String COLUMN_DATE = "date";

    public static DbManager getInstance() {
        return INSTANCE;
    }

    public static void setContext(Context context) {
        INSTANCE.context = context;
    }

    private final Executor executor = Executors.newSingleThreadExecutor();

    private Context context;

    private SQLiteDatabase database;

    public <T extends BaseModel> void insert(final T model) {
        executor.execute(() -> insertInternal(model));
    }

    public void select(BaseModel.Type type, DbListener dbListener, int limit, int offset) {
        executor.execute(() -> selectInternal(type, dbListener, limit, offset));
    }

    public void clean() {
        executor.execute(this::cleanInternal);
    }

    private void checkInitialized() {
        if (database != null) {
            return;
        }

        SQLiteOpenHelper helper = new SQLiteOpenHelper(context, "Vk.db", null, VERSION) {

            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + DIALOGS_TABLE + " ("
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
                insertDialog((DialogModel) model);
                break;
            case MESSAGE:
                break;
        }
    }

    private void insertDialog(DialogModel model) {
        // todo: to config
        int limit = 100;
        String title = model.getTitle();
        String message = model.getMessage();
        message = message.length() > limit ? message.substring(0, limit) : message;
        String imageUrl = model.getImageUrl();
        int date = model.getDate();
        int peerId = model.getPeerId();
        ContentValues values = new ContentValues();
        values.put(DIALOGS_COLUMN_PEER_ID, peerId);
        values.put(DIALOGS_COLUMN_TITLE, title);
        values.put(DIALOGS_COLUMN_MESSAGE, message);
        values.put(DIALOGS_COLUMN_IMAGE_URL, imageUrl);
        values.put(COLUMN_DATE, date);

        long rowId = database.insertWithOnConflict(DIALOGS_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (rowId == -1) {
            values.remove(DIALOGS_COLUMN_PEER_ID);
            database.update(DIALOGS_TABLE, values, DIALOGS_COLUMN_PEER_ID + "=" + peerId, null);
        }
    }

    private void selectInternal(BaseModel.Type type, DbListener dbListener, int limit, int offset) {
        checkInitialized();

        switch (type) {
            case DIALOG:
                getDialogs(dbListener, limit, offset);
            case MESSAGE:
                break;
        }
    }

    private void getDialogs(DbListener dbListener, int limit, int offset) {
        // LIMIT x, y == LIMIT y OFFSET x
        Cursor cursor = database.query(DIALOGS_TABLE, null, null, null, null, null, COLUMN_DATE + " DESC", offset + "," + limit);
        if (cursor == null) {
            return;
        }

        final List<DialogModel> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                DialogModel dialogModel = new DialogModel();
                dialogModel.setTitle(cursor.getString(cursor.getColumnIndex(DIALOGS_COLUMN_TITLE)));
                dialogModel.setMessage(cursor.getString(cursor.getColumnIndex(DIALOGS_COLUMN_MESSAGE)));
                dialogModel.setImageUrl(cursor.getString(cursor.getColumnIndex(DIALOGS_COLUMN_IMAGE_URL)));
                dialogModel.setPeerId(cursor.getInt(cursor.getColumnIndex(DIALOGS_COLUMN_PEER_ID)));
                dialogModel.setDate(cursor.getInt(cursor.getColumnIndex(COLUMN_DATE)));
                result.add(dialogModel);
            }
        } finally {
            cursor.close();
        }

        dbListener.callback(result);
    }

    private void cleanInternal() {
        checkInitialized();

        database.execSQL("DELETE FROM " + DIALOGS_TABLE);
    }

    public interface DbListener extends Listener {
        <T extends BaseModel> void callback(List<T> models);
    }
}
