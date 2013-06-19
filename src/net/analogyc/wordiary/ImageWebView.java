package net.analogyc.wordiary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;

public class ImageWebView extends WebView {

	Context context;
	GestureDetector gestureDetector;

	public ImageWebView(Context context) {
		super(context);
		this.context = context;

		setGestureDetector();
	}

	public ImageWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;

		setGestureDetector();
	}

	public ImageWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		setGestureDetector();
	}

	public void setGestureDetector() {
		gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				ImageWebView.this.zoomIn();
				return true;
			}
		});
	}

	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
	}
}