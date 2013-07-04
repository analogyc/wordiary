package net.analogyc.wordiary;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.analogyc.wordiary.dialog.ConfirmDialogFragment;
import net.analogyc.wordiary.dialog.ConfirmDialogFragment.ConfirmDialogListener;

/**
 * Displays the full image in a custom WebView to zoom on it
 */
public class ImageActivity extends BaseActivity implements ConfirmDialogListener {
	private int dayId;
	private String currentImage;
	private ImageWebView imageWebView;
	private TextView dateText;
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("dayId", dayId);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if(savedInstanceState.containsKey("dayId")){
			dayId = savedInstanceState.getInt("dayId");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setView();
	}

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

		// all custom onFlingListener for ImageWebView
		imageWebView.setOnFlingListener(new ImageWebView.OnFlingListener() {
			@Override
			public boolean onFling(View view, MotionEvent e1, MotionEvent motionEvent, float velocityX, float velocityY) {
				if (velocityX > 800f * imageWebView.getWebDensity()) {
					getNext(false);
				} else if (velocityX < -800f) {
					getNext(true);
				}

				return true;
			}
		});

		Cursor c = dataBase.getDayById(dayId);
		c.moveToFirst();
		String location = "file://" + c.getString(1);
		setCurrentImage(location);

		String dateString = c.getString(2);
		SimpleDateFormat format_in = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
		SimpleDateFormat format_out = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

		try {
			Date date = format_in.parse(dateString);
			dateText.setText(format_out.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		c.close();

		imageWebView.setImage(location);
	}

	/**
	 * Allows opening the next image
	 *
	 * @param view
	 */
	public void onNextImageButtonClicked(View view) {
		getNext(true);
	}

	/**
	 * Allows opening the previous image
	 *
	 * @param view
	 */
	public void onPrevImageButtonClicked(View view) {
		getNext(false);
	}

	/**
	 * Allows sharing the current image image
	 *
	 * @param view
	 */
	public void onShareImageButtonClicked(View view) {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/*");
		share.putExtra(Intent.EXTRA_STREAM, Uri.parse(currentImage));
		startActivity(Intent.createChooser(share, getString(R.string.share_via)));
	}

	/**
	 * Deletes the image
	 *
	 * @param view
	 */
	public void onDeleteImageButtonClicked(View view){
		Cursor day = dataBase.getDayById(dayId);
		day.moveToFirst();
		int dayId = day.getInt(0);
		day.close();
		//control if photo is editable
		if (dataBase.isEditableDay(dayId)){
			//ask if user really wants to proceed
			ConfirmDialogFragment newFragment = new ConfirmDialogFragment();
			newFragment.setId(0);
			newFragment.show(getSupportFragmentManager(), "Confirm");
		}
		else{
			Toast toast = Toast.makeText(getBaseContext(), R.string.grace_period_ended, TOAST_DURATION_S);
			toast.show();
		}
	}
	
	/**
	 * If user confirms the operation delete the photo
	 * 
	 * @param dialog
	 * @param id the dialog id
	 * 
	 */
	public void onConfirmedClick(DialogFragment dialog, int id) {
		//get photo filename
		Cursor day = dataBase.getDayById(dayId);
		day.moveToFirst();
		int dayId = day.getInt(0);
		String filename = day.getString(1);
		day.close();
		//delete photo
		File photo = new File(filename);
		if(photo.delete()){
			dataBase.deletePhoto(dayId);
			Toast toast = Toast.makeText(getBaseContext(), R.string.photo_deleted ,TOAST_DURATION_S);
			toast.show();
		}
		finish();
		
	}

	/**
	 * Goes back if the image is not zoomed, or unzooms the image
	 */
	@Override
	public void onBackPressed() {
		if (imageWebView.getScale() == imageWebView.getWebDensity()) {
			super.onBackPressed();
		} else {
			setView();
		}
	}

}