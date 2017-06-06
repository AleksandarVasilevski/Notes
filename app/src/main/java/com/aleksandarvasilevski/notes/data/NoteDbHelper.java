package com.aleksandarvasilevski.notes.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aleksandarvasilevski.notes.data.NoteContract.NoteEntry;

public class NoteDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 2;

    public NoteDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_CREATE_NOTES_TABLE = "CREATE TABLE " + NoteEntry.TABLE_NAME + "("
            + NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NoteEntry.COLUMN_TITLE + " TEXT, "
            + NoteEntry.COLUMN_DESCRIPTION + " TEXT, "
            + NoteEntry.COLUMN_DATE + " TEXT);";

    private static final String SQL_TWO = "ALTER TABLE "
            + NoteEntry.TABLE_NAME + " ADD COLUMN " + NoteEntry.COLUMN_DATE + " TEXT;";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2)
        db.execSQL(SQL_TWO);
    }
}
