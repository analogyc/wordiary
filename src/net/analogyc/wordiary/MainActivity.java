package net.analogyc.wordiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ExpandableListView;
import android.widget.Toast;
import net.analogyc.wordiary.OptionEntryDialogFragment.OptionEntryDialogListener;
import net.analogyc.wordiary.models.EntryListAdapter;
import net.analogyc.wordiary.models.EntryListAdapter.OptionDayListener;
import net.analogyc.wordiary.models.EntryListAdapter.OptionEntryListener;

import java.util.ArrayList;

/**
 * Displays the list of days as parents and entries as children
 */
public class MainActivity extends BaseActivity implements OptionEntryDialogListener,OptionEntryListener,OptionDayListener{

	private ExpandableListView entryList;
	private long[] expandedIds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the the corresponding link for each view object
        entryList = (ExpandableListView) findViewById(R.id.entries);
        
    }

	/**
	 * Reloads the list of entries
	 * 
	 * 
	 */
	protected void showEntries(){
		EntryListAdapter entryAdapter = new EntryListAdapter(this, bitmapWorker);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int typefaceInt = Integer.parseInt(preferences.getString("typeface", "1"));
		Typeface typeface;
		switch (typefaceInt) {
			case 2:
				typeface = Typeface.createFromAsset(getAssets(), "fonts/animeace2.ttf");
				break;
			case 3:
				typeface = Typeface.createFromAsset(getAssets(), "fonts/stanhand.ttf");
				break;
			default:
				typeface = Typeface.SANS_SERIF;
		}

		int fontSize = Integer.parseInt(preferences.getString("font_size", "2"));
		int textSize;
		switch (fontSize) {
			case 1:
				textSize = 14;
				break;
			case 3:
				textSize = 24;
				break;
			default:
				textSize = 18;
		}
		entryAdapter.setChildFont(typeface, textSize);
		
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
	 * Opens the selected image in fullscreen
	 *
	 * @param id The entry id
	 */
	public void onDayLongClicked(int id){
		Intent intent = new Intent(MainActivity.this, ImageActivity.class);
    	intent.putExtra("dayId",id);
    	startActivity(intent);
	}

	/**
	 * Removes the entry and reloads the entry list
	 *
	 * @param id The id of the entry to delete
	 */
	@Override
	public void deleteSelectedEntry(int id) {
		dataBase.deleteEntryById(id);
		Toast toast = Toast.makeText(this, getString(R.string.message_deleted), TOAST_DURATION_S);
		toast.show();
		showEntries();

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
		startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
	}
	
	
	@Override
    protected void onStart() {
        super.onStart();
        showEntries();
        if (expandedIds != null) {
        	restoreListState();
        }
    }

    @Override
    protected void onPause() {
        expandedIds = getExpandedIds();
        super.onPause();
    }
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//store expanded days
		outState.putLongArray("ExpandedIds", getExpandedIds());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		
		if (savedState.containsKey("ExpandedIds")) {
			expandedIds = savedState.getLongArray("ExpandedIds");
			restoreListState();
		}
	}
	
	
	private long[] getExpandedIds() {
			EntryListAdapter adapter = (EntryListAdapter)(entryList.getExpandableListAdapter());
			int length = adapter.getGroupCount();
			ArrayList<Long> ids = new ArrayList<Long>();
			
			for(int i=0; i < length; i++) {
				if(entryList.isGroupExpanded(i)) {
					ids.add(adapter.getGroupId(i));
				}
			}
			long[] expandedIds = new long[ids.size()];
	        int i = 0;
	        for (Long e : ids)  
	        	expandedIds[i++] = e.longValue();
	        return expandedIds;
	}
	
	public void restoreListState() {
		EntryListAdapter adapter = (EntryListAdapter)(entryList.getExpandableListAdapter());
		long id;
		if (expandedIds != null && adapter != null) {
			for (long l : expandedIds) {
				for (int i=0; i<adapter.getGroupCount(); i++) {
					id = adapter.getGroupId(i);
					if (l == id) {
						entryList.expandGroup(i);
						break;
					}
				}

			}
		}
	}
	
}
