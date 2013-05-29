package net.analogyc.wordiary.models;

import net.analogyc.wordiary.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
 
 
public class ImageAdapter extends BaseAdapter {
	private Context context;
	private final String[] moods;
 
	public ImageAdapter(Context context, String[] mobileValues) {
		this.context = context;
		this.moods = mobileValues;
	}
 
	public View getView(int position, View convertView, ViewGroup parent) {
 
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View gridView;
		
		if (convertView == null) {
			 
			gridView = new View(context);
 
			// get layout from mobile.xml
			gridView = inflater.inflate(R.layout.moods_style, null);
 
			// set image based on selected text
			ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
			//get the identifier of the image
			int identifier = context.getResources().getIdentifier(moods[position], "drawable", R.class.getPackage().getName());
			imageView.setImageResource(identifier);
 
		} else {
			//it won't happen
			gridView = (View) convertView;
		}
 
		return gridView;
	}
 
	@Override
	public int getCount() {
		return moods.length;
	}
 
	@Override
	public Object getItem(int position) {
		return null;
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
	}
 
}