package net.analogyc.wordiary.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import net.analogyc.wordiary.R;
import net.analogyc.wordiary.database.DBAdapter;
import net.analogyc.wordiary.models.BitmapWorker;
import java.util.ArrayList;

/**
 * Adapter to show each entry in the gallery
 */
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

	/**
	 * Shows each image
	 *
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View gridView = inflater.inflate(R.layout.image_style, null);

		// set image based on selected text
		final ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_gallery);
		String photoPath = photos.get(position)[1];
		int dayId = Integer.parseInt(photos.get(position)[0]);
		int size = 192;

		imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				imageView.setMaxHeight(imageView.getMeasuredWidth());
				return true;
			}
		});

		bitmapWorker.createTask(imageView, photoPath)
			.setShowDefault(dayId)
			.setTargetHeight(size)
			.setTargetWidth(size)
			.setCenterCrop(true)
			.setHighQuality(true)
			.setRoundedCorner(15)
			.setPrefix("gallery_")
			.execute();

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