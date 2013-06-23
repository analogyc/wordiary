package net.analogyc.wordiary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import net.analogyc.wordiary.models.DBAdapter;

public class ImageActivity extends Activity {
	private int dayId;
	private DBAdapter dataBase;
	private ImageWebView imageWebView;
	private float scale = 1.f;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

		if (dayId == 0) {
			Intent intent = getIntent();
			dayId = intent.getIntExtra("dayId", -1);
		}

		dataBase = new DBAdapter(this);
		imageWebView = (ImageWebView) findViewById(R.id.imageWebView);
	}

	public void getNext(boolean backwards) {
		Cursor c = dataBase.getNextDay(dayId, backwards);
		if (c.getCount() == 1) {
			c.moveToNext();
			dayId = c.getInt(0);
			setView();
		}
	}


	public void setView() {
		String location;
		if (dayId == -1) {
			location = "file:///android_asset/default-avatar.jpg";
		} else {
			Cursor c = dataBase.getDayById(dayId);
			c.moveToFirst();
			if (c.getString(1) == "") {
				location = "file:///android_asset/default-avatar.jpg";
			} else {
				location = "file://" + c.getString(1);
			}
			c.close();
		}

		imageWebView.setImage(location);

		// all custom onFlingListener for ImageWebView
		imageWebView.setOnFlingListener(new ImageWebView.OnFlingListener() {
			@Override
			public boolean onFling(View view, MotionEvent e1, MotionEvent motionEvent, float velocityX, float velocityY) {
				Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
				DisplayMetrics dm = new DisplayMetrics();
				display.getMetrics(dm);

				if ((dm.densityDpi > 300 && imageWebView.getScale() == 2.0f)
					|| dm.densityDpi <= 300 && imageWebView.getScale() == 1.0f) {

					if (velocityX > 1000f) {
						getNext(false);
					} else if (velocityX < -1000f) {
						getNext(true);
					}
				}

				return true;
			}
		});
	}

	@Override
	public void onBackPressed() {
		// we are getting this 2.0 scale because of hdpi phones.
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);

		if ((dm.densityDpi > 300 && imageWebView.getScale() == 2.0f)
			|| dm.densityDpi <= 300 && imageWebView.getScale() == 1.0f) {
			super.onBackPressed();
		} else {
			setView();
		}
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
		outState.putFloat("scale", scale);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		if(savedInstanceState.containsKey("dayId")){
			dayId = savedInstanceState.getInt("dayId");
			scale = savedInstanceState.getFloat("scale");
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