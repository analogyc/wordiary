package net.analogyc.wordiary;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

public class HeaderView extends LinearLayout {

	LayoutInflater inflater;
	Button takePhotoButton, newEntryButton;

	public HeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.header, this, true);

		Typeface fontawsm = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");
		takePhotoButton = (Button) findViewById(R.id.takePhotoButton);
		newEntryButton = (Button) findViewById(R.id.newEntryButton);

		takePhotoButton.setTypeface(fontawsm);
		newEntryButton.setTypeface(fontawsm);
	}
}