package net.analogyc.wordiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ConfirmDialogFragment extends DialogFragment {

	public interface ConfirmDialogListener {
		public void onConfirmedClick(DialogFragment dialog, int id);
	}

	protected ConfirmDialogListener mListener;
	protected int id;
	
	
	public void setId (int id){
		this.id = id;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (ConfirmDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement NewEntryDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final int callId = this.id;

		builder.setTitle(R.string.title_confirm)
				// Add action buttons
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						mListener.onConfirmedClick(ConfirmDialogFragment.this, callId);
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						ConfirmDialogFragment.this.getDialog().cancel();
					}
				});

		return builder.create();
	}

}