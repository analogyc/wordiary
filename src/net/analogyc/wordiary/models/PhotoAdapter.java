package net.analogyc.wordiary.models;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import net.analogyc.wordiary.R;

import java.io.IOException;
import java.util.ArrayList;
 
 
public class PhotoAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String[]> photos = new ArrayList<String[]>();
	private BitmapWorker bitmapWorker;
 
	public PhotoAdapter(Context context, BitmapWorker bitmapWorker) {
		this.context = context;
		this.bitmapWorker = bitmapWorker;

		DBAdapter database = new DBAdapter(context);
		Cursor photos_db = database.getAllPhotos();
		String[] info;

		while(photos_db.moveToNext()){
			info = new String[2];
			info[0] = photos_db.getString(0);
			info[1] = photos_db.getString(1);
			photos.add(info);
		}

		database.close();
		photos_db.close();
	}
 
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View gridView;
		
		// get layout from mobile.xml
		gridView = inflater.inflate(R.layout.image_style, null);

		// set image based on selected text
		ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_gallery);

		int size = 128;

		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);

		if (dm.densityDpi > 300) {
			size = 256;
		}

		try {
			bitmapWorker.createTask(imageView, photos.get(position)[1])
				.setDefaultBitmap(BitmapFactory.decodeStream(context.getAssets().open("default-avatar.jpg")))
				.setTargetHeight(size)
				.setTargetWidth(size)
				.setCenterCrop(true)
				.setHighQuality(true)
				.setRoundedCorner(15)
				.setPrefix("gallery_")
				.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return gridView;
	}
 
	@Override
	public int getCount() {
		return photos.size();
	}
 
	@Override
	public Object getItem(int position) {
		return null;
	}
 
	@Override
	public long getItemId(int position) {
		return Long.parseLong(photos.get(position)[0]);
	}
 
}