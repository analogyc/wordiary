package net.analogyc.wordiary;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
	private ImageWebView imageWebView;
	private float scale = 1.f;
	private float relativeX;
	private float relativeY;

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


	public void setView() {
		InputStream imageStream;
		Bitmap image;
		String location;
		if (dayId == -1) {
			location = "file://android_asset/default-avatar.jpg";
		} else {
			Cursor c = dataBase.getDayById(dayId);
			c.moveToFirst();
			location = "file://" + c.getString(1);
			c.close();
		}

		imageWebView.setImage(location);
	}

	/*@Override
	public void onBackPressed() {
		if (scale != 1.0f) {
			Animation scaleAnimation = new ScaleAnimation(scale, 1.0f, scale, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnimation.setFillAfter(true);
			scaleAnimation.setDuration(250);
			imageView.startAnimation(scaleAnimation);
			scale = 1.0f;
			relativeX = 0;
			relativeY = 0;
		} else {
			super.onBackPressed();
		}
	}*/

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