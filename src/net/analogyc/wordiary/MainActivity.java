package net.analogyc.wordiary;

import android.graphics.Typeface;
import android.widget.*;
import android.widget.ExpandableListView.OnChildClickListener;
import net.analogyc.wordiary.models.BitmapWorker;
import net.analogyc.wordiary.models.DBAdapter;
import net.analogyc.wordiary.models.EntryListAdapter;
import net.analogyc.wordiary.models.Photo;
import android.support.v4.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends FragmentActivity implements NewEntryDialogFragment.NewEntryDialogListener {

 
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	//view links
	private ExpandableListView entryList;
	
	private Uri imageUri;
	private DBAdapter dataBase;
	private EntryListAdapter entryAdapter;
	private BitmapWorker bitmapWorker;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        
        //get the the corresponding link for each view object
        entryList = (ExpandableListView) findViewById(R.id.entries);

		Typeface fontawsm = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
		((Button) findViewById(R.id.takePhotoButton)).setTypeface(fontawsm);
		((Button) findViewById(R.id.newEntryButton)).setTypeface(fontawsm);
		((Button) findViewById(R.id.newMoodButton)).setTypeface(fontawsm);

		bitmapWorker = BitmapWorker.findOrCreateBitmapWorker(getSupportFragmentManager());
        dataBase = new DBAdapter(this);
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
            	Intent intent = new Intent(this, PreferencesActivity.class);
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
            	dataBase.addPhoto(imageUri.getPath());
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" + imageUri, Toast.LENGTH_LONG).show();
				bitmapWorker.clearBitmapFromMemCache(imageUri.getPath());
			} else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }
    
    //method used when user clicks on a entry
    public void onEntryClicked(int entryId){    	
         
        //open Entry Activity
        Intent intent = new Intent(MainActivity.this, EntryActivity.class);
    	intent.putExtra("entryId", entryId);
    	startActivity(intent);
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
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		if(imageUri != null){
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
		dataBase.close();
		//entryAdapter.getCursor().close();
		super.onPause();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		showEntries();		
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
	
	private void showEntries(){
		entryAdapter = new EntryListAdapter(this, bitmapWorker);
		entryList.setAdapter(entryAdapter);

		entryList.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
					int arg2, int arg3, long arg4) {
				onEntryClicked((int)arg4);
				return false;
			}
	 });
   }
    
}
