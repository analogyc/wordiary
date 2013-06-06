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
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import net.analogyc.wordiary.models.BitmapWorker;
import net.analogyc.wordiary.models.DBAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.Menu;
import android.widget.TextView;
import net.analogyc.wordiary.models.Photo;

public class EntryActivity extends BaseActivity {
	private final int MOOD_RESULT_CODE = 101;
	private int entryId;
	private int dayId;
	private TextView messageText, dateText;
    private ImageView photoButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);
		
		
		Intent intent = getIntent();
		//normally entryId can't be -1
		entryId = intent.getIntExtra("entryId", entryId);

		messageText = (TextView) findViewById(R.id.messageText);
		dateText = (TextView) findViewById(R.id.dateText);
        photoButton = (ImageView) findViewById(R.id.photoButton);
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.getString("typeface", "1");
		int typefaceInt = Integer.parseInt(preferences.getString("typeface", "1"));
		switch (typefaceInt) {
		case 2: Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/animeace2.ttf");
		messageText.setTypeface(typeface);
			break;
		case 3: Typeface typeface3 = Typeface.createFromAsset(getAssets(), "fonts/stanhand.ttf");
		messageText.setTypeface(typeface3);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MOOD_RESULT_CODE) {
			if (resultCode == RESULT_OK) {
				String moodId = data.getStringExtra("moodId");
				dataBase.updateMood(entryId, moodId);

			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	private void setView(){
  		Cursor c_entry = dataBase.getEntryById(entryId);
  		if (! c_entry.moveToFirst()) {
			 throw new RuntimeException("Wrong entry id");
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
		int dayId = c_entry.getInt(1);
		this.dayId = dayId;
        Bitmap image;
        InputStream image_stream;
        try {
            if (dayId == -1) {
                image_stream = getAssets().open("default-avatar.jpg");
				image = BitmapFactory.decodeStream(image_stream);
				image = Bitmap.createScaledBitmap(image, 256, 256, false);
				photoButton.setImageBitmap(image);
            } else {
            	Cursor c_photo = dataBase.getDayById(dayId);
            	c_photo.moveToFirst();
				bitmapWorker.createTask(photoButton, c_photo.getString(1))
					.setTargetHeight(photoButton.getWidth())
					.setTargetWidth(photoButton.getWidth())
					.setHighQuality(true)
					.setCenterCrop(true)
					.execute();
				c_photo.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

		c_entry.close();
  		//in the future we will get an image and a mood in the same way		
	}

	public void onPhotoButtonClicked(View view) {
		Intent intent = new Intent(this, ImageActivity.class);
		intent.putExtra("dayId", dayId);
		startActivity(intent);
	}

	public void onMoodButtonClicked(View view){
		Intent intent = new Intent(this, MoodsActivity.class);
		intent.putExtra("entryId", entryId);
		startActivityForResult(intent, MOOD_RESULT_CODE);
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
	protected void onResume(){
		super.onResume();
		setView();
	}

}
