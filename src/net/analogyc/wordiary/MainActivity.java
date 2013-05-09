package net.analogyc.wordiary;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {

	private ListView mainListView;
	private ArrayAdapter<String> listAdapter; 
	
	//view links
	private Button entryButton;
	private EditText entryText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mainListView = (ListView) findViewById(R.id.listView1);
        
        String[] days = new String[] { "Today", "Yesterday", "2 days ago", "3 days ago", 
                "29/03/2013", "28/03/2013", "27/03/2013", "26/03/2013"}; 
        
        ArrayList<String> daysList = new ArrayList<String>();  
        daysList.addAll(Arrays.asList(days)); 
        
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, daysList);
        
        mainListView.setAdapter(listAdapter);
        
        
        //get the the corresponding link for each view object
        entryButton = (Button) findViewById(R.id.newEntryButton);
        entryText = (EditText) findViewById(R.id.entryText);

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
    	/*
    	String message = entryText.getText().toString();
    	Intent intent = new Intent(MainActivity.this, DayActivity.class);
    	intent.putExtra("entryText", message);
    	startActivity(intent);
    	*/
    }
    
    
    //method used when "takePhoto" button is clicked
    public void onTakePhotoClicked(View view){
    	/*
    	Intent intent = new Intent(MainActivity.this, TakePhotoActivity.class);
    	startActivity(intent);
    	*/	
    }
    
    
    //method used when user clicks on a entry
    public void onEntryClicked(View view){
    	
    	//-- get entry id or timestamp to identify the clicked entry 
    	Intent intent = new Intent(MainActivity.this, EntryActivity.class);
    	//intent.putExtra("entryId", ----);
    	startActivity(intent);
    }
    
    
    
}
