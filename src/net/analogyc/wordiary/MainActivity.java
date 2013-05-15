package net.analogyc.wordiary;

import net.analogyc.wordiary.models.DBAdapter;
import net.analogyc.wordiary.models.Photo;
import android.support.v4.app.DialogFragment;
import net.analogyc.wordiary.models.EntryAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements NewEntryDialogFragment.NewEntryDialogListener{

 
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	//view links
	private ListView entryList;
	
	private Uri imageUri;
	private DBAdapter dataBase;
	private EntryAdapter entryAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //get the the corresponding link for each view object
        entryList = (ListView) findViewById(R.id.listView1);
        
        //open database connection
        dataBase = new DBAdapter(this);
        dataBase.open();
        
        showEntries();
      	
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
    	NewEntryDialogFragment newFragment = new NewEntryDialogFragment();
    	newFragment.show(getSupportFragmentManager(), "entry");
    }
    
    
    //method used when "takePhoto" button is clicked
    public void onTakePhotoClicked(View view){
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    	
    	imageUri = Uri.fromFile(Photo.getOutputMediaFile(1));
    	intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
    	startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" + imageUri, Toast.LENGTH_LONG).show();
                dataBase.addPhoto(imageUri.getPath());
                imageUri = null;
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }
    
    //method used when user clicks on a entry
    public void onEntryClicked(int entryId){    	
    	// get the id for the selected entry
        Cursor c = dataBase.getAllEntries();
        c.moveToPosition(entryId);
        int id = c.getInt(0);
        
        //open Entry Activity
        Intent intent = new Intent(MainActivity.this, EntryActivity.class);
    	intent.putExtra("entryId", id);
    	startActivity(intent);
    	
    	c.close();
    }


	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		Context context = getApplicationContext();
		CharSequence text;
		int duration = Toast.LENGTH_SHORT;
		    
		EditText edit=(EditText)dialog.getDialog().findViewById(R.id.newMessage);
        String message=edit.getText().toString();
        
		if(message != ""){
			text = "Message saved";
			
			dataBase.addEntry(message, 0);
			showEntries(); 
			
		} else {
			text = "Message not saved";
			
		}
		
		Toast toast1 = Toast.makeText(context, text, duration);
		toast1.show();
	}	
	
	public void moods(View view){
		Intent intent = new Intent(this, MoodsActivity.class);
    	startActivity(intent);
	}
	


	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		if( imageUri != null){
			outState.putString("cameraImageUri", imageUri.toString());
		}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		if(savedInstanceState.containsKey("cameraImageUri")){
			imageUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		dataBase.close();
		dataBase = null;
		entryAdapter.getCursor().close();
		entryAdapter = null;
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		if(dataBase == null){
			dataBase = new DBAdapter(this);
			dataBase.open();
		}
      	//this will set entryAdapter
		showEntries();
	}
		
	
    @Override  
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
    	super.onCreateContextMenu(menu, v, menuInfo);  
    	menu.setHeaderTitle("Entry Menu");  
    	menu.add(0, v.getId(), 0, "Share");  
    	menu.add(0, v.getId(), 0, "Delete");  
    } 
    
	@Override  
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
    } 
	
    private void showEntries(){
    	Cursor entries = dataBase.getAllEntries();
      	startManagingCursor(entries);
      	entryAdapter = new EntryAdapter(this, entries);
      	entryList.setAdapter(entryAdapter);
                
        entryList.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int pos, long row) {
                    onEntryClicked(pos);
            }
        });
        
        registerForContextMenu( entryList );
    }
    
}
