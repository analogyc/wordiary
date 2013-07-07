package net.analogyc.wordiary.dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.analogyc.wordiary.R;

/**
 * Allows editing an Entry
 */
public class EditEntryDialogFragment extends DialogFragment {

    /**
     * The interface for a edit entry dialog listener
     */
    public interface EditEntryDialogListener {
        /**
         * Manages a change request
         *
         * @param message the modified entry message
         */
        public void onDialogModifyClick(String message);
    }

    protected EditEntryDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (EditEntryDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement EditEntryDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        //get the current entry message
        String message = getArguments().getString("message");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_custom, null);
        builder.setView(view)
                .setTitle(R.string.dialog_edit_entry)
                        // Add action buttons
                .setPositiveButton(R.string.go, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText edit = (EditText) ((EditEntryDialogFragment.this).getDialog().findViewById(R.id.newMessage));
                        mListener.onDialogModifyClick(edit.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditEntryDialogFragment.this.getDialog().cancel();
                    }
                });

        if (message != null) {
            ((TextView) view.findViewById(R.id.newMessage)).setText(message);
        }

        return builder.create();
    }
}
