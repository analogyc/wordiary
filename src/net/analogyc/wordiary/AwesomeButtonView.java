package net.analogyc.wordiary;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class AwesomeButtonView extends Button {

	private static Typeface fontAwesome;

	public AwesomeButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (fontAwesome == null) {
			fontAwesome = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");
		}

		setTypeface(fontAwesome);
	}
}
