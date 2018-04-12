package com.caezar.vklite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    public static DbManager getInstance(Context context) {
        INSTANCE.context = context.getApplicationContext();
        return INSTANCE;
    }

    private final Executor executor = Executors.newSingleThreadExecutor();

    private Context context;

    private SQLiteDatabase database;

    public void insert(final String title, final String message, final String imageUrl) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                insertInternal(title, message, imageUrl);
            }
        });
    }

    public void readAll(final ReadAllListener<String> listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                readAllInternal(listener);
            }
        });
    }

    public void clean() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                cleanInternal();
            }
        });
    }

    private void checkInitialized() {
        if (database != null) {
            return;
        }

        SQLiteOpenHelper helper = new SQLiteOpenHelper(context, "Vk.db", null, VERSION) {

            @Override
            public void onCreate(SQLiteDatabase db) {
                createDatabase(db);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        };

        database = helper.getWritableDatabase();
    }

    private void createDatabase(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_DIALOGS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DIALOGS_COLUMN_TITLE + " TEXT NOT NULL, "
                + DIALOGS_COLUMN_MESSAGE + " TEXT NOT NULL,"
                + DIALOGS_COLUMN_IMAGE_URL + " TEXT NOT NULL);"
                );
    }

    private void insertInternal(String title, String message, String imageUrl) {
        checkInitialized();

        database.execSQL("INSERT INTO " + TABLE_DIALOGS + " (" + DIALOGS_COLUMN_TITLE + "," + DIALOGS_COLUMN_MESSAGE + "," + DIALOGS_COLUMN_IMAGE_URL + ") VALUES (?,?,?)", new Object[]{title, message, imageUrl});
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
