package net.analogyc.wordiary;

import net.analogyc.wordiary.models.DataBaseHelper;
import net.analogyc.wordiary.models.EntryAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity 
implements NewEntryDialogFragment.NewEntryDialogListener{

 

	
	//view links
	private Button entryButton;
	private EditText entryText;
	private ListView entryList;
	
	DataBaseHelper dataBaseHelper;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //get the the corresponding link for each view object
        entryButton = (Button) findViewById(R.id.newEntryButton);
        entryText = (EditText) findViewById(R.id.entryText);
        entryList = (ListView) findViewById(R.id.listView1);
        
        //show the entries stored in the db
      	dataBaseHelper = new DataBaseHelper(this);
      	Cursor c = dataBaseHelper.getAllEntries();
      	startManagingCursor(c);
      	entryList.setAdapter(new EntryAdapter(this, c));
        
                
        entryList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int pos, long row) {
                    // Remembers the selected Index
                    onEntryClicked(pos);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
            	Intent intent = new Intent(this, SettingsActivity.class);
            	startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    //method used when "newEntryButton" button is clicked
    public void onNewEntryButtonClicked(View view){
    	/*String message = entryText.getText().toString();
    	if(message != ""){
    		dataBaseHelper.addEntry(message, 0);
    		
    		Cursor c = dataBaseHelper.getAllEntries();
    		startManagingCursor(c);
    		entryList.setAdapter(new EntryAdapter(this, c));
    		entryText.setText("");
        	entryText.clearFocus();
    	}*/
    	
    	NewEntryDialogFragment newFragment = new NewEntryDialogFragment();
    	newFragment.show(getSupportFragmentManager(), "entry");
    }
    
    
    //method used when "takePhoto" button is clicked
    public void onTakePhotoClicked(View view){
    	/*
    	Intent intent = new Intent(MainActivity.this, TakePhotoActivity.class);
    	startActivity(intent);
    	*/	
    }
    
    
    //method used when user clicks on a entry
    public void onEntryClicked(int entryId){    	
    	// get the id for the selected entry
        Cursor c = dataBaseHelper.getAllEntries();
        c.moveToPosition(entryId);
        int id = c.getInt(0);
        
        //open Entry Activity
        Intent intent = new Intent(MainActivity.this, EntryActivity.class);
    	intent.putExtra("entryId", id);
    	startActivity(intent);
    }


	@Override
	public void onDialogPositiveClick(
			android.support.v4.app.DialogFragment dialog) {
		// TODO Auto-generated method stub
		Context context = getApplicationContext();
		CharSequence text = "Hello toast!";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
		
	}


	@Override
	public void onDialogNegativeClick(
			android.support.v4.app.DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}
    
    
    
}
