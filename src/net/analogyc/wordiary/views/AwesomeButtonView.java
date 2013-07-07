package net.analogyc.wordiary.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class AwesomeButtonView extends Button {

	private static Typeface sFontAwesome;

	public AwesomeButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (sFontAwesome == null) {
			sFontAwesome = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");
		}

		setTypeface(sFontAwesome);
	}
}
