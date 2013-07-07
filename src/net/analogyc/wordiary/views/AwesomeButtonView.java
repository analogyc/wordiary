package net.analogyc.wordiary.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * A button that uses AwesomeFont to show symbols
 */
public class AwesomeButtonView extends Button {

    private static Typeface sFontAwesome;

    private final static String TYPEFACE_DIR = "fonts/fontawesome-webfont.ttf";

    public AwesomeButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (sFontAwesome == null) {
            sFontAwesome = Typeface.createFromAsset(context.getAssets(), TYPEFACE_DIR);
        }

        setTypeface(sFontAwesome);
    }
}
