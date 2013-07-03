package net.analogyc.wordiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import net.analogyc.wordiary.R;
 
 
public class MoodsAdapter extends BaseAdapter {
	private Context context;
	private final String[] moods;
 
	public MoodsAdapter(Context context, String[] moods) {
		this.context = context;
		this.moods = moods;
	}

	/**
	 * Each view contains one mood from the res directory
	 *
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
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
			gridView = (View) convertView;
		}
 
		return gridView;
	}

	/**
	 * Get the amount of moods
	 * @return
	 */
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