package net.analogyc.wordiary;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import net.analogyc.wordiary.models.DBAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageActivity extends Activity {
	private int dayId;
	private DBAdapter dataBase;
	private ImageView imageView;
	private GestureDetector gestureDetector;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

		Intent intent = getIntent();
		dayId = intent.getIntExtra("dayId", -1);

		dataBase = new DBAdapter(this);

		gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				Toast.makeText(ImageActivity.this, "double tap", Toast.LENGTH_SHORT).show();
				return true;
			}
		});

		imageView = (ImageView) findViewById(R.id.fullImageView);
	}

	public void setView() {
		InputStream imageStream;
		Bitmap image;
		try {
			if (dayId == -1) {
				imageStream = getAssets().open("default-avatar.jpg");
			} else {
				Cursor c = dataBase.getDayById(dayId);
				c.moveToFirst();
				imageStream = new FileInputStream(new File(c.getString(1)));
				c.close();
			}

			image = BitmapFactory.decodeStream(imageStream);

			// can't display sizes over 2048x2048 on a Galaxy Nexus... who knows about other Androids
			if (image.getWidth() > 1600 || image.getHeight() > 1600) {
				int targetWidth, targetHeight;

				if (image.getWidth() > image.getHeight()) {
					targetWidth = 1600;
					targetHeight = image.getWidth() / 1600 * image.getHeight();
				} else {
					targetHeight = 1600;
					targetWidth = image.getHeight() / 1600 * image.getWidth();
				}
				image = Bitmap.createScaledBitmap(image, targetWidth, targetHeight, false);
			}

			imageView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {
					if (!gestureDetector.onTouchEvent(motionEvent)) {
						return view.onTouchEvent(motionEvent);
					}
					return true;
				}
			});

			imageView.setImageBitmap(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return true;
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
		outState.putInt("dayId", dayId);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		if(savedInstanceState.containsKey("dayId")){
			dayId = savedInstanceState.getInt("dayId");
		}
	}

	@Override
	protected void onPause(){
		dataBase.close();
		super.onPause();
	}

	@Override
	protected void onResume(){
		super.onResume();
		setView();
	}
}