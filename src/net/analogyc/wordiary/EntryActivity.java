package net.analogyc.wordiary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageButton;
import net.analogyc.wordiary.models.DBAdapter;
import net.analogyc.wordiary.models.DataBaseHelper;
import net.analogyc.wordiary.models.EntryAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class EntryActivity extends Activity {
	private int entryId;
	private DBAdapter dataBase;
	private TextView messageText, dateText;
    private ImageButton photoButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);
		
		Intent intent = getIntent();
		
		//normally entryId can't be -1
		entryId = intent.getIntExtra("entryId", -1);
		
		messageText = (TextView) findViewById(R.id.messageText);
		dateText = (TextView) findViewById(R.id.dateText);
        photoButton = (ImageButton) findViewById(R.id.photoButton);
	}
	
	private void setView(){
  		Cursor c_entry = dataBase.getEntryById(entryId);
  		if (! c_entry.moveToFirst()) {
  			//error! wrong ID, but it won't happen
  		}
  		String message = c_entry.getString(2);
  		messageText.setText(message);
  		
  		String d_tmp = c_entry.getString(4);
  		SimpleDateFormat format_in = new SimpleDateFormat("yyyyMMddHHmmss",Locale.ITALY);
  		SimpleDateFormat format_out = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy",Locale.ITALY);
		try {
			Date date = format_in.parse(d_tmp);
			dateText.setText(format_out.format(date)); //probably a better method to do this exists
		} catch (ParseException e) {
			//won't happen if we use only dataBaseHelper.addEntry(...)
		}  
		
        Cursor c_photo = dataBase.getPhotoByDay(d_tmp.substring(0, 8));

        Bitmap image;
        InputStream image_stream;
        try {
        	c_photo.moveToFirst();
        	String filename = c_photo.getString(1);
        	
            if (filename.equals("")) {
                image_stream = getAssets().open("default-avatar.jpg");
            } else {
                image_stream = new FileInputStream(new File(filename));
            }

            image = BitmapFactory.decodeStream(image_stream);
            image = Bitmap.createScaledBitmap(image, 128, 128, false);
            photoButton.setImageBitmap(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

		c_entry.close();
        c_photo.close();
  		//in the future we will get an image and a mood in the same way		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entry, menu);
		return true;
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putInt("entryId", entryId);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		if(savedInstanceState.containsKey("entryId")){
			entryId = savedInstanceState.getInt("entryId");
		}
	}
	
	
	@Override
	protected void onPause(){
		super.onPause();
		dataBase.close();
		dataBase = null;
	}
	
	@Override
	protected void onResume(){
		super.onResume();

        dataBase = new DBAdapter(this);
        dataBase.open();

		setView();
	}

}
