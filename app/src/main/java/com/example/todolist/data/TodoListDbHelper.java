package com.example.todolist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class TodoListDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "todolist.db";


    public TodoListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold to-do.  A to-do is a task that need to be completed.
        final String SQL_CREATE_TODO_TABLE = "CREATE TABLE " + TodoListContract.TodoEntry.TABLE_NAME + " (" +
                TodoListContract.TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TodoListContract.TodoEntry.COLUMN_DATETEXT + " INTEGER, " +
                TodoListContract.TodoEntry.COLUMN_DESC + " TEXT NOT NULL, " +
                TodoListContract.TodoEntry.COLUMN_DUE_DATE_TEXT + " INTEGER, " +
                TodoListContract.TodoEntry.COLUMN_DONE + " INTEGER, " +
                "UNIQUE (" + TodoListContract.TodoEntry.COLUMN_DESC + ", " + TodoListContract.TodoEntry.COLUMN_DATETEXT +
                ") ON " + "CONFLICT IGNORE" + " ); ";

        sqLiteDatabase.execSQL(SQL_CREATE_TODO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TodoListContract.TodoEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
