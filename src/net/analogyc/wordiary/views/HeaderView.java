package net.analogyc.wordiary.views;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import net.analogyc.wordiary.R;

/**
 * The blue header that appears through most of the application
 */
public class HeaderView extends LinearLayout {

    private LayoutInflater mInflater;
    private Button mTakePhotoButton;

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.header, this, true);

        mTakePhotoButton = (Button) findViewById(R.id.takePhotoButton);

        if (!(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
                || context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))) {
            mTakePhotoButton.setTextColor(0x77FFFFFF);
        }
    }
}