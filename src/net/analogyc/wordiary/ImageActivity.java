package net.analogyc.wordiary;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
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

		gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				Animation scaleAnimation = new ScaleAnimation(scale, scale * 1.5f, scale, scale * 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

				float tempRelativeX = (relativeX + e.getX() - (imageView.getWidth() / 2)) * 1.5f;
				float tempRelativeY = (relativeY + e.getY() - (imageView.getHeight() / 2)) * 1.5f;
				TranslateAnimation transAnimation = new TranslateAnimation(-relativeX, -tempRelativeX, -relativeY, -tempRelativeY);
				relativeX = tempRelativeX;
				relativeY = tempRelativeY;

				AnimationSet set = new AnimationSet(true);
				set.addAnimation(scaleAnimation);
				set.addAnimation(transAnimation);
				set.setFillAfter(true);
				set.setDuration(250);

				imageView.startAnimation(set);
				scale = scale * 1.5f;
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

				// width : height = x : 1600
				if (image.getWidth() > image.getHeight()) {
					targetWidth = 1600;
					targetHeight = 1600 * image.getHeight() / image.getWidth();
				} else {
					targetHeight = 1600;
					targetWidth = 1600 * image.getWidth() / image.getHeight();
				}

				image = Bitmap.createScaledBitmap(image, targetWidth, targetHeight, false);
			}

			imageView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {
					return gestureDetector.onTouchEvent(motionEvent);
				}
			});

			imageView.setImageBitmap(image);
			Animation scaleAnimation = new ScaleAnimation(scale, scale, scale, scale, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			TranslateAnimation transAnimation = new TranslateAnimation(0, -relativeX, 0, -relativeY);
			AnimationSet set = new AnimationSet(true);
			set.addAnimation(scaleAnimation);
			set.addAnimation(transAnimation);
			set.setFillAfter(true);
			set.setDuration(0);

			imageView.startAnimation(set);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
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