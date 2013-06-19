package net.analogyc.wordiary;

import android.widget.*;
import android.widget.ExpandableListView.OnChildClickListener;
import net.analogyc.wordiary.models.EntryListAdapter;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends BaseActivity {

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
		EditEntryDialogFragment editFragment = new EditEntryDialogFragment();
		editFragment.show(getSupportFragmentManager(), "editEntry");
	}


	protected void showEntries(){
		entryAdapter = new EntryListAdapter(this, bitmapWorker);
		entryList.setAdapter(entryAdapter);
		
	}

		
	/*
	THIS CODE NEEDS TO BE REPLACED WITH A FRAGMENT DIALOG
	  
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
    	super.onCreateContextMenu(menu, v, menuInfo);  
    	menu.setHeaderTitle("Entry Menu");  
    	menu.add(0, v.getId(), 0, "Share");  
    	menu.add(0, v.getId(), 0, "Delete");  
    } 
    
   public boolean onContextItemSelected(MenuItem item) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		
		//get the id of the selected entry
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int number=(int)entryAdapter.getItemId(info.position);
		if(item.getTitle()=="Delete"){
			//delete the entry
			dataBase.deleteEntry(number);
			showEntries();
			Toast toast1 = Toast.makeText(context, "Message deleted", duration);
			toast1.show();
        } 
		return true;
    } */
}
