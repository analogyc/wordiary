package net.analogyc.wordiary.adapters;

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
    private Context mContext;
    private ArrayList<String[]> mPhotos = new ArrayList<String[]>();
    private BitmapWorker mBitmapWorker;

    /**
     * Create a new photoAdapter
     *
     * @param context      the activity context
     * @param bitmapWorker a bitmapWorker to manage image loading
     */
    public PhotoAdapter(Context context, BitmapWorker bitmapWorker) {
        mContext = context;
        mBitmapWorker = bitmapWorker;

        DBAdapter database = new DBAdapter(context);
        Cursor photos_db = database.getAllPhotos();
        String[] info;

        while (photos_db.moveToNext()) {
            info = new String[2];
            info[0] = photos_db.getString(0);
            info[1] = photos_db.getString(1);
            mPhotos.add(info);
        }

        database.close();
        photos_db.close();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView = inflater.inflate(R.layout.image_style, null);

        // set image based on selected text
        final ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_gallery);
        String photoPath = mPhotos.get(position)[1];
        int dayId = Integer.parseInt(mPhotos.get(position)[0]);
        int size = 192;

        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imageView.setMaxHeight(imageView.getMeasuredWidth());
                return true;
            }
        });

        mBitmapWorker.createTask(imageView, photoPath)
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
        return mPhotos.size();
    }

    @Override
    public Object getItem(int position) {
        return mPhotos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mPhotos.get(position)[0]);
    }

}