package com.example.todolist;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.data.TodoListContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AddTaskDialogFragment.AddTaskDialogListener, TodoCursorAdapter.ToggleTodoCheckListener {

    TodoCursorAdapter todoCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setHasFixedSize(true);

        todoCursorAdapter = new TodoCursorAdapter(null, this);
        mRecyclerView.setAdapter(todoCursorAdapter);

        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_btn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddTaskDialogFragment addTaskDialogFragment = new AddTaskDialogFragment();
                addTaskDialogFragment.show(getSupportFragmentManager(), "addTask");
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);

        // Bravo

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        return new CursorLoader(this, TodoListContract.TodoEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        todoCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        todoCursorAdapter.swapCursor(null);
    }

    @Override
    public void onDialogPositiveClick(String stringValue) {

        if (!TextUtils.isEmpty(stringValue)) {
            getContentResolver().insert(TodoListContract.TodoEntry.CONTENT_URI, getTodoListContentValues(stringValue));
        }
    }

    @Override
    public void onDialogNegetiveClick(DialogFragment dialogFragment) {


    }

    @Override
    public void onTodoItemChange(int todoID, boolean done) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(TodoListContract.TodoEntry.COLUMN_DONE,  done ? 1 : 0);
        String[] mSelectionArgs = { Integer.toString(todoID) };
        getContentResolver().update(TodoListContract.TodoEntry.CONTENT_URI, contentValues, TodoListContract.TodoEntry.WHERE_TODO_ID, mSelectionArgs);

    }

    private ContentValues getTodoListContentValues(String stringValues) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(TodoListContract.TodoEntry.COLUMN_DESC, stringValues);
        contentValues.put(TodoListContract.TodoEntry.COLUMN_DATETEXT, Calendar.getInstance().getTimeInMillis());

        return contentValues;
    }

}