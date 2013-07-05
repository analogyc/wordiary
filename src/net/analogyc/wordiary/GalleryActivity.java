package net.analogyc.wordiary;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.analogyc.wordiary.adapters.PhotoAdapter;

/**
 * Shows the images on a grid
 */
public class GalleryActivity extends BaseActivity {

	private GridView gridView;
	
	@Override
	public void onStart() {
		super.onStart();
		setView();
	}

	protected void setView() {
		//set a new content view
		setContentView(R.layout.activity_gallery);

		gridView = (GridView) findViewById(R.id.photoGrid);
		gridView.setAdapter(new PhotoAdapter(this, bitmapWorker));

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adpView, View view, int position,long id) {
				//open the image in fullscreen mode
				Intent intent = new Intent(GalleryActivity.this, ImageActivity.class);
				intent.putExtra("dayId", (int)id);
				startActivity(intent);
			}
		});
		
		//if there's no photo show a message
		if(gridView.getAdapter().getCount() <= 0){
			RelativeLayout layout =(RelativeLayout) findViewById(R.id.galleryLayout);
			TextView tv = new TextView(this);
			tv.setText(R.string.no_photo);
			tv.setTextColor(0xFFBBBBBB);
			tv.setTextSize(34);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
			params.addRule(RelativeLayout.BELOW, R.id.HeaderViewLayout);
			layout.addView(tv, params);
		}
	}
	
	/**
	 * Takes results from: camera intent (100), and update view
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				//If everything ok, update view
				this.setView();
			}
		}
	}

    /**
     * The home button shouldn't do anything when already in GalleryActivity
     *
     * @param view
     */
    @Override
    public void onOpenGalleryClicked(View view) {
        // prevent a new gallery from appearing
    }
}
