package net.analogyc.wordiary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ExpandableListView;
import android.widget.Toast;
import net.analogyc.wordiary.OptionEntryDialogFragment.OptionEntryDialogListener;
import net.analogyc.wordiary.models.EntryListAdapter;

public class MainActivity extends BaseActivity implements OptionEntryDialogListener{

	//view links
	private ExpandableListView entryList;
	private EntryListAdapter entryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the the corresponding link for each view object
        entryList = (ExpandableListView) findViewById(R.id.entries);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    //method used when user clicks on a entry
    public void onEntryClicked(int entryId) {
        //open Entry Activity
        Intent intent = new Intent(MainActivity.this, EntryActivity.class);
    	intent.putExtra("entryId", entryId);
    	startActivity(intent);
    }

	@Override
	protected void onResume(){
		super.onResume();
		showEntries();
	}


	public void onEntryLongClicked(int id){
		OptionEntryDialogFragment editFragment = new OptionEntryDialogFragment();
		Bundle args = new Bundle();
		args.putInt("entryId", id);
		editFragment.setArguments(args);
		editFragment.show(getSupportFragmentManager(), "editEntry");
	}


	protected void showEntries(){
		entryAdapter = new EntryListAdapter(this, bitmapWorker);
		entryList.setAdapter(entryAdapter);
	}


	@Override
	public void deleteSelectedEntry(int id) {
		dataBase.deleteEntry(id);
		showEntries();
		Toast toast = Toast.makeText(this, "Message deleted", 2000);
		toast.show();
	}


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

}
