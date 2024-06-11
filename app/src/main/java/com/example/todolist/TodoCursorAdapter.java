package com.example.todolist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.data.TodoListContract;

/**
 * Created by Sheeraz.
 */

public class TodoCursorAdapter extends RecyclerView.Adapter<TodoCursorAdapter.MyViewHolder> {

    private Cursor cursor;
    private ToggleTodoCheckListener toggleTodoCheckListener;

    public TodoCursorAdapter(Cursor cursor, Context context) {

        Cursor _cursor;

        try {
            // Instantiate the ToggleTodoCheckListener so we can send events to the host
            toggleTodoCheckListener = (ToggleTodoCheckListener) context;
        } catch (ClassCastException classCastException) {

            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement ToggleTodoCheckListener");
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.todo_item, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        cursor.moveToPosition(position);
        holder.bindView(cursor);

    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor _cursor) {
        this.cursor = _cursor;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView todoDescTextView;
        private CheckBox todoTaskCheckBox;
        private int todoTaskID = -1;


        public MyViewHolder(@NonNull View view) {
            super(view);
            todoDescTextView = view.findViewById(R.id.todo_item_description);
            todoTaskCheckBox = view.findViewById(R.id.todo_item_checkbox);
        }

        @SuppressLint("Range")
        private void bindView(Cursor cursor) {

            todoTaskID = cursor.getInt(cursor.getColumnIndex(TodoListContract.TodoEntry._ID));
            boolean isTaskDone = cursor.getInt(cursor.getColumnIndex(TodoListContract.TodoEntry.COLUMN_DONE)) == 1;
            String description = cursor.getString(cursor.getColumnIndex(TodoListContract.TodoEntry.COLUMN_DESC));
            todoDescTextView.setText(description);
            toggleTask(isTaskDone);
            todoTaskCheckBox.setChecked(isTaskDone);

            todoTaskCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                    toggleTask(isTaskDone);
                    toggleTodoCheckListener.onTodoItemChange(todoTaskID, b);
                }
            });

        }

        private void toggleTask(boolean done) {

            if (done) {
                todoDescTextView.setPaintFlags(todoDescTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            } else {

                todoDescTextView.setPaintFlags(todoDescTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

//            toggleTodoCheckListener.onTodoItemChange(todoTaskID, done);
        }

    }

/* The activity that uses an instance of this adapter must
* implement this interface in order to receive event callbacks. */
public interface ToggleTodoCheckListener {

    void onTodoItemChange(int todoID, boolean done);
}
}
