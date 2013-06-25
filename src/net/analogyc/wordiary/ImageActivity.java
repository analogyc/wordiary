package net.analogyc.wordiary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import net.analogyc.wordiary.models.DBAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Displays the full image in a custom WebView to zoom on it
 */
public class ImageActivity extends BaseActivity {
	private int dayId;
	private String currentImage;
	private ImageWebView imageWebView;
	private TextView dateText;
	private Button nextButton, prevButton, shareButton;

	/**
	 * Opens the next or previous image if available
	 *
	 * @param backwards If we actually want a getPrev
	 */
	public void getNext(boolean backwards) {
		Cursor c = dataBase.getNextDay(dayId, backwards);
		if (c.getCount() == 1) {
			c.moveToNext();
			dayId = c.getInt(0);
			setView();
		}
		c.close();
	}

	/**
	 * Get currently loaded image
	 *
	 * @return The uri to the currently loaded image
	 */
	public String getCurrentImage() {
		return currentImage;
	}

	public void setCurrentImage(String image) {
		currentImage = image;
	}

	/**
	 * Prepares the views and loads the image
	 */
	public void setView() {
		// reload the entire view since on android 2.x it will fail to reload the html
		setContentView(R.layout.activity_image);

		if (dayId == 0) {
			Intent intent = getIntent();
			dayId = intent.getIntExtra("dayId", -1);
		}

		imageWebView = (ImageWebView) findViewById(R.id.imageWebView);
		dateText = (TextView) findViewById(R.id.imageDateText);
		nextButton = (Button) findViewById(R.id.nextImageButton);
		prevButton = (Button) findViewById(R.id.prevImageButton);
		shareButton = (Button) findViewById(R.id.shareImageButton);
		
		// all custom onFlingListener for ImageWebView
		imageWebView.setOnFlingListener(new ImageWebView.OnFlingListener() {
			@Override
			public boolean onFling(View view, MotionEvent e1, MotionEvent motionEvent, float velocityX, float velocityY) {
				Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
				DisplayMetrics dm = new DisplayMetrics();
				display.getMetrics(dm);

				if ((dm.densityDpi > 300 && imageWebView.getScale() == 2.0f) || dm.densityDpi <= 300 && imageWebView.getScale() == 1.0f) {
					if (velocityX > 800f) {
						getNext(false);
					} else if (velocityX < -800f) {
						getNext(true);
					}
				}

				return true;
			}
		});
		
		Typeface fontawsm = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
		nextButton.setTypeface(fontawsm);
		prevButton.setTypeface(fontawsm);
		shareButton.setTypeface(fontawsm);

		String location;
		if (dayId == -1) {
			location = "file://android_asset/default-avatar.jpg";
		} else {
			Cursor c = dataBase.getDayById(dayId);
			c.moveToFirst();
			if (c.getString(1).equals("")) {
				location = "file://android_asset/default-avatar.jpg";
			} else {
				location = "file://" + c.getString(1);
			}

			setCurrentImage(location);

			String dateString = c.getString(2);
			SimpleDateFormat format_in = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ITALY);
			SimpleDateFormat format_out = new SimpleDateFormat("dd.MM.yyyy", Locale.ITALY);

			try {
				Date date = format_in.parse(dateString);
				dateText.setText(format_out.format(date));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			c.close();
		}
		
		imageWebView.setImage(location);
	}

	/**
	 * Allows opening the next image
	 *
	 * @param view
	 */
	public void onNextImageButtonClicked(View view) {
		Log.e("onNext", "next");
		getNext(true);
	}

	/**
	 * Allows opening the previous image
	 *
	 * @param view
	 */
	public void onPrevImageButtonClicked(View view) {
		Log.e("onPrev", "prev");
		getNext(false);
	}

	/**
	 * Allows sharing the current image image
	 *
	 * @param view
	 */
	public void onShareImageButtonClicked(View view) {
		Intent share = new Intent(Intent.ACTION_SEND);

		String ext = currentImage.substring(currentImage.length() - 4, currentImage.length()).toLowerCase();
		Log.e("ext", ext);
		if (ext.equals(".jpg") || ext.equals("jpeg")) {
			share.setType("image/jpeg");
		} else if (ext.equals(".png")) {
			share.setType("image/png");
		}

		share.putExtra(Intent.EXTRA_STREAM, Uri.parse(currentImage));
		startActivity(Intent.createChooser(share, "Share Image"));
	}

	/**
	 * Goes back if the image is not zoomed, or unzooms the image
	 */
	@Override
	public void onBackPressed() {
		// we are getting this 2.0 scale because of hdpi phones.
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);

		if ((dm.densityDpi > 300 && imageWebView.getScale() == 2.0f) || dm.densityDpi <= 300 && imageWebView.getScale() == 1.0f) {
			super.onBackPressed();
		} else {
			setView();
		}
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
	protected void onResume(){
		super.onResume();
		setView();
	}
}