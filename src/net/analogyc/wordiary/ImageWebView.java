package net.analogyc.wordiary;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.lang.reflect.Field;

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

		overrideZoom();
	}

	public boolean zoomIn() {
		overrideZoom();
		return super.zoomIn();
	}

	public void overrideZoom() {
		// infinite zoom for 2.2~ the exception handles it if this doesn't exist
		Class<?> webViewClass = this.getClass().getSuperclass();
		try {
			Field mMaxZoomScale = webViewClass.getDeclaredField("mMaxZoomScale");
			mMaxZoomScale.setAccessible(true);
			mMaxZoomScale.set(this, 10000f);
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		}

		// infinite zoom for 3.0-4.0~
		// from http://stackoverflow.com/a/10496816/644504, couldn't get my hands on one
		try {
			Field mZoomManagerField = webViewClass.getDeclaredField("mZoomManager");
			mZoomManagerField.setAccessible(true);
			Object mZoomManagerInstance = mZoomManagerField.get(this);

			Class<?> zoomManagerClass = Class.forName("android.webkit.ZoomManager");
			Field mDefaultMaxZoomScaleField = zoomManagerClass.getDeclaredField("mDefaultMaxZoomScale");
			mDefaultMaxZoomScaleField.setAccessible(true);
			mDefaultMaxZoomScaleField.set(mZoomManagerInstance, 10000f);
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		} catch (ClassNotFoundException e) {
		}

		// infinite zoom for 4.2~
		try {
			// retrieve the ZoomManager from the WebView
			Field mProviderField = webViewClass.getDeclaredField("mProvider");
			mProviderField.setAccessible(true);
			Object mProviderInstance = mProviderField.get(this);

			Class<?> mProviderClass = mProviderInstance.getClass();
			Field mZoomManagerField = mProviderClass.getDeclaredField("mZoomManager");
			mZoomManagerField.setAccessible(true);
			Object mZoomManagerInstance = mZoomManagerField.get(mProviderInstance);

			Class<?> zoomManagerClass = Class.forName("android.webkit.ZoomManager");
			Field mDefaultMaxZoomScaleField = zoomManagerClass.getDeclaredField("mDefaultMaxZoomScale");
			mDefaultMaxZoomScaleField.setAccessible(true);
			mDefaultMaxZoomScaleField.set(mZoomManagerInstance, 10000f);
			Field mMaxZoomScaleField = zoomManagerClass.getDeclaredField("mMaxZoomScale");
			mMaxZoomScaleField.setAccessible(true);
			mMaxZoomScaleField.set(mZoomManagerInstance, 10000f);
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		} catch (ClassNotFoundException e) {
		}
	}

	public void setImage(String location) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);

		// for some reason higher DPI must screw with the width of WebView
		if (dm.densityDpi > 300) {
			dm.widthPixels /= 2;
		}

		String html =
			"<html>" +
				"<head>" +
				"<meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; minimum-scale=1.0; maximum-scale=10000.0 target-densitydpi=device-dpi;\">" +
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
		overrideZoom();
	}

	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
	}
}