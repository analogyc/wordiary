package net.analogyc.wordiary;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class ImageWebView extends WebView {

	Context context;
	GestureDetector gestureDetector;

	public ImageWebView(Context context) {
		super(context);
		this.context = context;

		setup();
	}

	public ImageWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;

		setup();
	}

	public ImageWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		setup();
	}

	public void setup() {
		WebSettings set = getSettings();
		set.setAllowFileAccess(true);
		set.setJavaScriptEnabled(true);
		set.setBuiltInZoomControls(true);
		set.setLoadWithOverviewMode(true);
		set.setUseWideViewPort(true);
		setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		setScrollbarFadingEnabled(true);
		setBackgroundColor(Color.BLACK);

		gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				ImageWebView.this.zoomIn();
				return true;
			}
		});
	}

	public void setImage(String location) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);

		// for some reason
		if (dm.densityDpi > 300) {
			dm.widthPixels /= 2;
		}

		String html =
			"<html>" +
				"<head>" +
				"<meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; minimum-scale=1.0; maximum-scale=80.0; target-densitydpi=device-dpi;\">" +
				"<style>" +
					"html {background: #000000}" +
					"body {margin: 0; padding: 0;}" +
					"#wrapper {width: 100%; text-align:center}" +
				"</style></head>" +
				"<body>" +
					"<div id=\"wrapper\"><img width=\"" + dm.widthPixels + "\" src=\"" + location + "\" /></div>" +
				"</body>" +
				"</html>";

		loadDataWithBaseURL("", html, "text/html", "utf-8", "");
	}

	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
	}
}