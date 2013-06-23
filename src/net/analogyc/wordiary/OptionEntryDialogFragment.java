package net.analogyc.wordiary;


import net.analogyc.wordiary.NewEntryDialogFragment.NewEntryDialogListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class OptionEntryDialogFragment extends DialogFragment {
	
	private int entryId;
	
	public interface OptionEntryDialogListener {
		public void deleteSelectedEntry(int id);
		public void shareSelectedEntry(int id);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		entryId = getArguments().getInt("entryId");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_edit_entry, null);
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view).setTitle(R.string.dialog_option_entry);
		
		
		Button shareButton = (Button) view.findViewById(R.id.shareButton);
		Button deleteButton = (Button) view.findViewById(R.id.deleteButton);
		
		final OptionEntryDialogFragment entryDialog = this;
		shareButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((OptionEntryDialogListener)entryDialog.getActivity()).shareSelectedEntry(entryId);
				entryDialog.dismiss();
			}	
		});
		
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((OptionEntryDialogListener)entryDialog.getActivity()).deleteSelectedEntry(entryId);
				entryDialog.dismiss();
			}	
		});
		
		return builder.create();
	}
}
