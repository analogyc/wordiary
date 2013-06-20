package net.analogyc.wordiary.models;

import java.util.ArrayList;

import net.analogyc.wordiary.R;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
 
 
public class PhotoAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String[]> photos = new ArrayList<String[]>();
 
	public PhotoAdapter(Context context) {
		this.context = context;
		
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
		
		if (convertView == null) {
			 
			gridView = new View(context);
 
			// get layout from mobile.xml
			gridView = inflater.inflate(R.layout.moods_style, null);
			
			Drawable image = Drawable.createFromPath(photos.get(position)[1]);
			// set image based on selected text
			ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
			//get the identifier of the image
			
			imageView.setImageDrawable(image);
 
		} else {
			//it won't happen
			gridView = (View) convertView;
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