package com.example.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TodoListProvider extends ContentProvider {

    private static final int TODO = 100;
    private static final int TODO_ID = 101;

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TodoListDbHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {

        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TodoListContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        uriMatcher.addURI(authority, TodoListContract.PATH_TODO, TODO);
        uriMatcher.addURI(authority, TodoListContract.PATH_TODO + "/#", TODO_ID);

        return uriMatcher;

    }

    @Override
    public boolean onCreate() {

        mOpenHelper = new TodoListDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "to-do"
            case TODO: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TodoListContract.TodoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TODO_ID: {
                retCursor = mOpenHelper.getWritableDatabase().query(
                        TodoListContract.TodoEntry.TABLE_NAME,
                        projection,
                        TodoListContract.TodoEntry._ID + " = '"+ ContentUris.parseId(uri) +"'",
                        null,
                        null,
                        null,
                        sortOrder

                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case TODO:
                return TodoListContract.TodoEntry.CONTENT_TYPE;
            case TODO_ID:
                return TodoListContract.TodoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case TODO: {
                long _id = sqLiteDatabase.insert(TodoListContract.TodoEntry.TABLE_NAME, null, contentValues);

                if (_id > 0)
                    returnUri = TodoListContract.TodoEntry.buildTodoUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                    break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;


    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowDeleted;

        switch (match) {

            case TODO:
                rowDeleted = sqLiteDatabase.delete(TodoListContract.TodoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }

        // Because a null deletes all rows
        if (selection == null || rowDeleted != 0) {

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case TODO:
                rowsUpdated = sqLiteDatabase.update(TodoListContract.TodoEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] contentValues) {

        SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {

            case TODO:
                sqLiteDatabase.beginTransaction();
                int returnCount = 0;

                try {
                    for (ContentValues contentValue: contentValues) {
                        long _id = sqLiteDatabase.insert(TodoListContract.TodoEntry.TABLE_NAME, null, contentValue);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    sqLiteDatabase.setTransactionSuccessful();

                } finally {
                    sqLiteDatabase.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, contentValues);
        }


    }
}
