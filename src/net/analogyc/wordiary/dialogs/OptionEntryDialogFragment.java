package net.analogyc.wordiary.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import net.analogyc.wordiary.R;

public class OptionEntryDialogFragment extends DialogFragment {

    private int mEntryId;

    /**
     * The interface for a option entry dialog listener
     */
    public interface OptionEntryDialogListener {
        /**
         * Manages a delete entry request
         *
         * @param id the entry id
         */
        public void deleteSelectedEntry(int id);

        /**
         * Manages a new entry addition request
         *
         * @param id the entry id
         */
        public void shareSelectedEntry(int id);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mEntryId = getArguments().getInt("entryId");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_entry, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view).setTitle(R.string.dialog_option_entry);


        Button shareButton = (Button) view.findViewById(R.id.shareButton);
        Button deleteButton = (Button) view.findViewById(R.id.deleteButton);

        //maintaining a reference to this instance for next operations
        final OptionEntryDialogFragment entryDialog = this;

        final OptionEntryDialogListener activity;
        try {
            activity = (OptionEntryDialogListener) entryDialog.getActivity();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(entryDialog.getActivity().toString() + " must implement OptionEntryDialogListener");
        }


        //set the action from share button
        shareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.shareSelectedEntry(mEntryId);
                entryDialog.dismiss();
            }
        });

        //set the action from delete button
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.deleteSelectedEntry(mEntryId);
                entryDialog.dismiss();
            }
        });

        return builder.create();
    }
}
