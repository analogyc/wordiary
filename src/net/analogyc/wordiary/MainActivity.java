package net.analogyc.wordiary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;
import net.analogyc.wordiary.OptionEntryDialogFragment.OptionEntryDialogListener;
import net.analogyc.wordiary.models.EntryListAdapter;
import net.analogyc.wordiary.models.EntryListAdapter.OptionEntryListener;

/**
 * Displays the list of days as parents and entries as children
 */
public class MainActivity extends BaseActivity implements OptionEntryDialogListener,OptionEntryListener{

	private ExpandableListView entryList;
	private EntryListAdapter entryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the the corresponding link for each view object
        entryList = (ExpandableListView) findViewById(R.id.entries);
    }

	/**
	 * Reloads the list of entries
	 */
	protected void showEntries(){
		entryAdapter = new EntryListAdapter(this, bitmapWorker);
		entryList.setAdapter(entryAdapter);
	}

	/**
	 * Opens the entry in a new activity
	 *
	 * @param id The entry id
	 */
    public void onEntryClicked(int id) {
        //open Entry Activity
        Intent intent = new Intent(MainActivity.this, EntryActivity.class);
    	intent.putExtra("entryId", id);
    	startActivity(intent);
    }

	/**
	 * Gives two commands: Delete entry and Share entry
	 *
	 * @param id The entry id
	 */
	public void onEntryLongClicked(int id){
		OptionEntryDialogFragment editFragment = new OptionEntryDialogFragment();
		Bundle args = new Bundle();
		args.putInt("entryId", id);
		editFragment.setArguments(args);
		editFragment.show(getSupportFragmentManager(), "editEntry");
	}

	/**
	 * Removes the entry and reloads the entry list
	 *
	 * @param id The id of the entry to delete
	 */
	@Override
	public void deleteSelectedEntry(int id) {
		dataBase.deleteEntry(id);
		showEntries();
		Toast toast = Toast.makeText(this, "Message deleted", TOAST_DURATION_S);
		toast.show();
	}

	/**
	 * Allows sharing the text of the entry
	 *
	 * @param id The id of the entry
	 */
	@Override
	public void shareSelectedEntry(int id) {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		Cursor entry = dataBase.getEntryById(id);
		entry.moveToFirst();
		String shareBody = entry.getString(2);
		entry.close();
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Wordiary");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}

	@Override
	protected void onResume(){
		super.onResume();
		showEntries();
	}
}
