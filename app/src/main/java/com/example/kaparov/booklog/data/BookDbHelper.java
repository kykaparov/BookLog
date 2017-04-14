package com.example.kaparov.booklog.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kaparov.booklog.data.BookContract.*;

/**
 * Database helper for Books app. Manages database creation and version management.
 */
public class BookDbHelper extends SQLiteOpenHelper{


    /** Name of the database file */
    public static final String DATABASE_NAME = "library.db";

    public static final int DATABASE_VERSION = 1;
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME;

    /**
     * Constructs a new instance of {@link BookDbHelper}.
     *
     * @param context of the app
     */
    public BookDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + BookEntry.TABLE_NAME + " (" +
                BookEntry._ID +  " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BookEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                BookEntry.COLUMN_AUTHOR + " TEXT," +
                BookEntry.COLUMN_CATEGORY + " TEXT," +
                BookEntry.COLUMN_PAGES + " TEXT NOT NULL," +
//                BookEntry.COLUMN_IMAGE + " BLOB NOT NULL, " +
                BookEntry.COLUMN_RATING + " INTEGER NOT NULL DEFAULT 0)" ;

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
