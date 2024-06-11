package com.example.todolist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Sheeraz.
 */

public class AddTaskDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    AddTaskDialogListener addTaskDialogListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {

            // Instantiate the NoticeDialogListener so we can send events to the host
            addTaskDialogListener = (AddTaskDialogListener) context;

        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement AddTaskDialogListener");
        }
        }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Build the dialog and set up the button click handlers
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_create, null, false);
        final EditText textView = view.findViewById(R.id.new_task_edit);
        mBuilder.setView(view);
        mBuilder.setTitle(R.string.add_new_task)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Send the positive button event back to the host activity
                        addTaskDialogListener.onDialogPositiveClick(textView.getText().toString());
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Send the negative button event back to the host activity
                        addTaskDialogListener.onDialogNegetiveClick(AddTaskDialogFragment.this);
                    }
                });

        return mBuilder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
* implement this interface in order to receive event callbacks.
* Each method passes the DialogFragment in case the host needs to query it. */
public interface AddTaskDialogListener {

    public void onDialogPositiveClick(String inputValue);
    public void onDialogNegetiveClick(DialogFragment dialogFragment);
}

}
