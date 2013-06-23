package net.analogyc.wordiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import net.analogyc.wordiary.models.PhotoAdapter;

public class GalleryActivity extends BaseActivity {

	private GridView gridView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);

		gridView = (GridView) findViewById(R.id.photoGrid);
		
		gridView.setAdapter(new PhotoAdapter(this, bitmapWorker));
 
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Intent intent = new Intent(GalleryActivity.this, ImageActivity.class);
				intent.putExtra("dayId", (int)arg3);
				startActivity(intent);
			}
		});
		
	}
}
