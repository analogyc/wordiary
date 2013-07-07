package net.analogyc.wordiary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import net.analogyc.wordiary.R;

/**
 * Adapter to show the moods
 */
public class MoodsAdapter extends BaseAdapter {
    private Context mContext;
    private final String[] mMoods;

    /**
     * Create a new mood adapter
     *
     * @param context the activity context
     * @param moods   the list of moods
     */
    public MoodsAdapter(Context context, String[] moods) {
        super();
        mContext = context;
        mMoods = moods;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(mContext);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.moods_style, null);

            // set image based on selected text
            ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
            //get the identifier of the image
            int identifier = mContext.getResources().getIdentifier(mMoods[position], "drawable", R.class.getPackage().getName());
            imageView.setImageResource(identifier);

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return mMoods.length;
    }

    @Override
    public Object getItem(int position) {
        return mMoods[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}