package net.analogyc.wordiary.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.analogyc.wordiary.R;
import net.analogyc.wordiary.database.DBAdapter;
import net.analogyc.wordiary.models.BitmapWorker;
import net.analogyc.wordiary.models.DateFormats;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Adapter to show each day as a parent and each entry as a child of a day
 */
public class EntryListAdapter extends BaseExpandableListAdapter {

    /**
     * The interface for a day listener
     */
    public interface OptionDayListener {

        /**
         * Manages a long click on a day
         *
         * @param id the day id
         */
        public void onDayLongClicked(int id);
    }

    /**
     * The interface for a entry listener
     */
    public interface OptionEntryListener {

        /**
         * Manages a click on a entry
         *
         * @param id the entry id
         */
        public void onEntryLongClicked(int id);

        /**
         * Manages a long click on a entry
         *
         * @param id the entry id
         */
        public void onEntryClicked(int id);
    }

    private final Context mContext;
    private ArrayList<String[]> mDays = new ArrayList<String[]>();
    private BitmapWorker mBitmapWorker;
    private int mChildTextSize;
    private Typeface mChildTypeface;

    public EntryListAdapter(Context context, BitmapWorker bitmapWorker) {
        mContext = context;
        mBitmapWorker = bitmapWorker;

        //these explicit assignments make clear how setView(...) works with these variables
        mChildTypeface = null;
        mChildTextSize = 0;

        DBAdapter database = new DBAdapter(context);
        Cursor day = database.getAllDays();
        String[] info;
        while (day.moveToNext()) {
            info = new String[3];
            info[0] = day.getString(0);        //id
            info[1] = day.getString(1);        //filename
            info[2] = day.getString(2);        //data
            mDays.add(info);
        }
        database.close();
        day.close();
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        DBAdapter database = new DBAdapter(mContext);

        //get all entries associated to the day given
        Cursor entries = database.getEntriesByDay((int) getGroupId(groupPosition));
        String[] info = new String[4];
        entries.moveToFirst();
        if (entries.moveToPosition(childPosition)) {
            info[0] = entries.getString(0);        //id
            info[1] = entries.getString(2);        //message
            info[2] = entries.getString(3);        //mood
            info[3] = entries.getString(4);        //data
        }
        database.close();
        entries.close();
        return info;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        String[] child = (String[]) getChild(groupPosition, childPosition);
        return Long.parseLong(child[0]);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        String[] info = (String[]) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.entry_style, null);
        }
        TextView message = ((TextView) view.findViewById(R.id.entryMessage));
        message.setText(info[1]);

        //set a custom look for message if asked
        if (mChildTextSize != 0) {
            message.setTextSize(mChildTextSize);
        }
        if (mChildTypeface != null) {
            message.setTypeface(mChildTypeface);
        }

        final GestureDetector gestureDetector = new GestureDetector(mContext, new EntryGDetector(Integer.parseInt(info[0])));
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(0xFFFFFFFF);
                } else {
                    v.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                }

                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        DBAdapter database = new DBAdapter(mContext);
        Cursor entries = database.getEntriesByDay((int) getGroupId(groupPosition));
        int size = entries.getCount();
        database.close();
        entries.close();
        return size;

    }

    @Override
    public Object getGroup(int groupPosition) {
        return mDays.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mDays.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return Long.parseLong(mDays.get(groupPosition)[0]);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
        String[] info = (String[]) getGroup(groupPosition);
        Boolean hasImage = true;
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.day_style, null);
        }


        ImageView imageView = (ImageView) view.findViewById(R.id.dayImage);

        int entries = getChildrenCount(groupPosition);
        TextView v = (TextView) view.findViewById(R.id.dayCount);
        v.setText("" + entries);

        String path = null;
        if (!info[1].equals("")) {
            path = info[1];
        } else {
            hasImage = false;
        }

        mBitmapWorker.createTask(imageView, path)
                .setShowDefault(Integer.parseInt(info[0]))
                .setTargetHeight(128)
                .setTargetWidth(128)
                .setCenterCrop(true)
                .setHighQuality(true)
                .setRoundedCorner(15)
                .execute();

        SimpleDateFormat format_in = new SimpleDateFormat(DateFormats.DATABASE, Locale.getDefault());
        SimpleDateFormat format_out = new SimpleDateFormat(DateFormats.IMAGE, Locale.getDefault());
        try {
            Date date = format_in.parse(info[2]);
            ((TextView) view.findViewById(R.id.dayDate)).setText(format_out.format(date));
        } catch (ParseException e) {
            //won't happen if we use only dataBaseHelper.addEntry(...)
        }


        final GestureDetector gestureDetector = new GestureDetector(mContext, new DayGDetector(Integer.parseInt(info[0]), hasImage));

        imageView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        return view;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setChildFont(Typeface typeface, int textSize) {
        mChildTypeface = typeface;
        mChildTextSize = textSize;
    }


    private class EntryGDetector extends SimpleOnGestureListener {

        private int mId;
        private OptionEntryListener mActivity;


        public EntryGDetector(int id) {
            super();
            mId = id;
            try {
                mActivity = (OptionEntryListener) mContext;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(mContext.toString() + " must implement OptionEntryListener");
            }
        }

        @Override
        public void onLongPress(MotionEvent event) {
            mActivity.onEntryLongClicked(mId);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            mActivity.onEntryClicked(mId);
            return true;
        }
    }

    private class DayGDetector extends SimpleOnGestureListener {

        private int mId;
        private OptionDayListener mActivity;
        private boolean mLongClickEnabled;


        public DayGDetector(int id, boolean longClickEnabled) {
            super();
            mId = id;
            mLongClickEnabled = longClickEnabled;
            try {
                mActivity = (OptionDayListener) mContext;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(mContext.toString() + " must implement OptionDayListener");
            }
        }

        @Override
        public void onLongPress(MotionEvent event) {
            if (mLongClickEnabled) {
                mActivity.onDayLongClicked(mId);
            }
        }

    }


}
