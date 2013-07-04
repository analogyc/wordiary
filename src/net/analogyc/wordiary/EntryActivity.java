package net.analogyc.wordiary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import net.analogyc.wordiary.dialog.ConfirmDialogFragment;
import net.analogyc.wordiary.dialog.EditEntryDialogFragment;
import net.analogyc.wordiary.dialog.ConfirmDialogFragment.ConfirmDialogListener;
import net.analogyc.wordiary.dialog.EditEntryDialogFragment.EditEntryDialogListener;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EntryActivity extends BaseActivity implements EditEntryDialogListener, ConfirmDialogListener  {

	private final int MOOD_RESULT_CODE = 101;
	private int entryId;
	private int dayId;
	private TextView messageText, dateText;
    private ImageView photoButton, moodImage;
	private Button setNewMoodButton, editEntryButton, photoDeleteButton;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);
		
		Intent intent = getIntent();
		//get the id of the selected entry (normally entryId can't be -1)
		entryId = intent.getIntExtra("entryId", -1);
	}

	/**
	 * Catch mood result code (101) or send it over to BaseActivity for other codes (currently, photo -> 100)
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MOOD_RESULT_CODE) {
			if (resultCode == RESULT_OK) {
				String moodId = data.getStringExtra("moodId");
				dataBase.updateMood(entryId, moodId);
				setView();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	/**
	 * Sets up the view content
	 */
	protected void setView(){		
		setContentView(R.layout.activity_entry);


		messageText = (TextView) findViewById(R.id.messageText);
		dateText = (TextView) findViewById(R.id.dateText);
        photoButton = (ImageView) findViewById(R.id.photoButton);
        moodImage = (ImageView) findViewById(R.id.moodImage);

		setNewMoodButton = (Button) findViewById(R.id.setNewMoodButton);
		editEntryButton = (Button) findViewById(R.id.editEntryButton);
		photoDeleteButton = (Button) findViewById(R.id.photoDeleteButton);
		
		if(!dataBase.isEditableEntry(entryId)){
			setNewMoodButton.setTextColor(0xFFBBBBBB);
			editEntryButton.setTextColor(0xFFBBBBBB);
		}

		// we keep this in onStart because the user might have changed the font in Preferences and come back to Entry
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int typefaceInt = Integer.parseInt(preferences.getString("typeface", "1"));
		switch (typefaceInt) {
			case 2:
				Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/animeace2.ttf");
				messageText.setTypeface(typeface);
				break;
			case 3:
				Typeface typeface3 = Typeface.createFromAsset(getAssets(), "fonts/stanhand.ttf");
				messageText.setTypeface(typeface3);
				break;
			default:
				messageText.setTypeface(Typeface.SANS_SERIF);
		}

		int fontSize = Integer.parseInt(preferences.getString("font_size", "2"));
		switch (fontSize) {
			case 1:
				messageText.setTextSize(14);
				break;
			case 3:
				messageText.setTextSize(24);
				break;
			default:
				messageText.setTextSize(18);
		}

  		Cursor c_entry = dataBase.getEntryById(entryId);
  		if (!c_entry.moveToFirst()) {
  			//won't happen if MainActivity uses correct entryIds
			 throw new RuntimeException("Wrong entry id");
  		}
  		String message = c_entry.getString(2);
  		messageText.setText(message);
  		String mood = c_entry.getString(3);

  		if(mood != null){
  			int identifier = getResources().getIdentifier(mood, "drawable", R.class.getPackage().getName());
			moodImage.setImageResource(identifier);
  		}

  		String d_tmp = c_entry.getString(4);
  		SimpleDateFormat format_in = new SimpleDateFormat("yyyyMMddHHmmss",Locale.ITALY);
  		SimpleDateFormat format_out = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy",Locale.ITALY);
		try {
			Date date = format_in.parse(d_tmp);
			dateText.setText(format_out.format(date)); //probably a better method to do this exists
		} catch (ParseException e) {
			//won't happen if we use only dataBase.addEntry(...)
			dateText.setText("??.??.????");
		}

		int dayId = c_entry.getInt(1);
		this.dayId = dayId;

		// make the image about square
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		photoButton.setMaxWidth(dm.widthPixels);
		photoButton.setMaxHeight(dm.widthPixels);

		if (dayId != -1) {
			Cursor c_photo = dataBase.getDayById(dayId);
			c_photo.moveToFirst();

			String path = null;
			if (!c_photo.getString(1).equals("")) {
				path = c_photo.getString(1);
			} else {
				photoButton.setClickable(false);
				photoDeleteButton.setVisibility(View.INVISIBLE);
			}

			bitmapWorker.createTask(photoButton, path)
				.setShowDefault(dayId)
				.setTargetHeight(dm.widthPixels)
				.setTargetWidth(dm.widthPixels)
				.setHighQuality(true)
				.setCenterCrop(true)
				.execute();
				c_photo.close();
		}

		c_entry.close();
		
		
		//check if this entry has a previous and a next 
		if(!dataBase.hasNextEntry(entryId, false)){
			Button nextB = (Button)this.findViewById(R.id.nextEntryButton);
			nextB.setClickable(false);
			nextB.setTextColor(0xFFBBBBBB);
		}
		if(!dataBase.hasNextEntry(entryId, true)){
			Button prevB = (Button)this.findViewById(R.id.prevEntryButton);
			prevB.setClickable(false);
			prevB.setTextColor(0xFFBBBBBB);
		}
	}

	/**
	 * Displays the full image
	 *
	 * @param view
	 */
	public void onPhotoButtonClicked(View view) {
		Intent intent = new Intent(this, ImageActivity.class);
		intent.putExtra("dayId", dayId);
		startActivity(intent);
	}

	/**
	 * Displays the mood activity, only if within the grace period
	 *
	 * @param view
	 */
	public void onMoodButtonClicked(View view){
		if(!dataBase.isEditableEntry(entryId)){
			Toast toast = Toast.makeText(getBaseContext(), getString(R.string.grace_period_ended), TOAST_DURATION_S);
			toast.show();
			return;
		}
		
		Intent intent = new Intent(this, MoodsActivity.class);
		intent.putExtra("entryId", entryId);
		startActivityForResult(intent, MOOD_RESULT_CODE);
	}

	/**
	 * Displays the panel for sharing the text of the entry
	 *
	 * @param view
	 */
	public void onShareButtonClicked(View view){
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String shareBody = (String) messageText.getText();
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Wordiary");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
	}

	/**
	 * Deletes the entry
	 *
	 * @param view
	 */
	public void onDeleteButtonClicked(View view){
		ConfirmDialogFragment newFragment = new ConfirmDialogFragment();
		newFragment.setId(0);
		newFragment.show(getSupportFragmentManager(), "Confirm");
		
	}
	
	/**
	 * Called after a confirmDialog, this method could delete a photo or  delete an entry
	 * 
	 * @param dialog
	 * @param id the dialog id
	 * 
	 */
	public void onConfirmedClick(DialogFragment dialog, int id) {
		
		// 0 = deleteEntry, 1 = DeletePhoto
		switch (id){
			case 0:	dataBase.deleteEntryById(entryId);
					Toast toast = Toast.makeText(getBaseContext(), getString(R.string.message_deleted),TOAST_DURATION_S);
					toast.show();
					//Now EntryActivity has no reason to be visible
					finish();
					break;
			case 1:	Cursor day = dataBase.getDayByEntry(entryId);
					day.moveToFirst();
					int dayId = day.getInt(0);
					String filename = day.getString(1);
					day.close();
					File photo = new File(filename);
					if(photo.delete()){
						dataBase.deletePhoto(dayId);
						Toast toast1 = Toast.makeText(getBaseContext(), R.string.photo_deleted ,TOAST_DURATION_S);
						toast1.show();
					}
					setView();
		}
	}
	
	/**
	 * Deletes the entry
	 *
	 * @param view
	 */
	public void onPhotoDelete(View view){
		Cursor day = dataBase.getDayByEntry(entryId);
		day.moveToFirst();
		int dayId = day.getInt(0);
		if (dataBase.isEditableDay(dayId)){
			ConfirmDialogFragment newFragment = new ConfirmDialogFragment();
			newFragment.setId(1);
			newFragment.show(getSupportFragmentManager(), "Confirm");
		}
		else{
			Toast toast = Toast.makeText(getBaseContext(), R.string.grace_period_ended ,TOAST_DURATION_S);
			toast.show();
		}
	}

	/**
	 * Displays the dialog for editing the entry, only if within the grace period
	 * @param view
	 */
	public void onEditButtonClicked(View view){
		if(!dataBase.isEditableEntry(entryId)){
			Toast toast = Toast.makeText(getBaseContext(), getString(R.string.grace_period_ended), TOAST_DURATION_S);
			toast.show();
			return;
		}
		
		EditEntryDialogFragment editFragment = new EditEntryDialogFragment();
		Bundle args = new Bundle();
		args.putString("message", (String) messageText.getText());
		editFragment.setArguments(args);
		editFragment.show(getSupportFragmentManager(), "modifyEntry");
	}

	/**
	 * Allows editing the content of the entry
	 *
	 * @param dialog
	 */
	public void onDialogModifyClick(DialogFragment dialog) {
		Context context = getApplicationContext();
		CharSequence text;

		EditText edit=(EditText)dialog.getDialog().findViewById(R.id.newMessage);
		String message = edit.getText().toString();

		if(!message.equals("")){
			text = getString(R.string.message_saved);
			dataBase.updateMessage(entryId, message);
			setView();
		} else {
			text = getString(R.string.message_not_saved);
		}

		Toast toast = Toast.makeText(context, text, TOAST_DURATION_S);
		toast.show();
	}
	
	/**
	 * Shows next entry
	 * 
	 * @param view
	 */
	public void nextEntryButtonClicked(View view){
		moveNext(false);
	}
	
	/**
	 * Shows previous entry
	 * 
	 * @param view
	 */
	public void prevEntryButtonClicked(View view){
		moveNext(true);
	}
	
	/**
	 * Provides and shows the informations of previous/next entry
	 * 
	 * @param backwards true to go back, false otherwise
	 */
	public void moveNext(boolean backwards){
		Cursor nextEntry = dataBase.getNextEntry(entryId, backwards);
		nextEntry.moveToFirst();
		int next = nextEntry.getInt(0);
		entryId = next;
		nextEntry.close();
		setView();
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
	protected void onStart(){
		super.onStart();
		setView();
	}
}
