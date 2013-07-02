package net.analogyc.wordiary.models;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import net.analogyc.wordiary.R;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Adapter to show each day as a parent and each entry as a child of a day
 */
public class EntryListAdapter extends BaseExpandableListAdapter {
	
	public interface OptionDayListener {
		public void onDayLongClicked(int id);
	}
	
	public interface OptionEntryListener {
		public void onEntryLongClicked(int id);
		public void onEntryClicked(int id);
	}

	private final Context context;
	private ArrayList<String[]> days = new ArrayList<String[]>();
	private BitmapWorker bitmapWorker;
	private int childTextSize;
	private Typeface childTypeface;

	public EntryListAdapter(Context context, BitmapWorker bitmapWorker) {
		this.context = context;
		this.bitmapWorker = bitmapWorker;
		
		//these explicit assignments make clear how setView works with these variables
		childTypeface =  null;
		childTextSize = 0;

		DBAdapter database = new DBAdapter(context);
		Cursor day = database.getAllDays();
		String[] info;
		while(day.moveToNext()){
			info = new String[3];
			info[0] = day.getString(0);
			info[1] = day.getString(1);
			info[2] = day.getString(2);
			days.add(info);
		}
		database.close();
		day.close();
	}
	

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		DBAdapter database = new DBAdapter(context);
		Cursor entries = database.getEntriesByDay((int)getGroupId(groupPosition));
		String[] info = new String[4];
		entries.moveToFirst();
		if(entries.moveToPosition(childPosition)){
			info[0] = entries.getString(0);
			info[1] = entries.getString(2);
			info[2] = entries.getString(3);
			info[3] = entries.getString(4);
		}
		else{
			info[0] = "";
			info[1] = "";
			info[2] = "";
			info[3] = "";
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
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.entry_style, null);
		}
		TextView  message = ((TextView) view.findViewById(R.id.entryMessage));
		message.setText(info[1]);
		
		//set a custom look for message if asked
		if(childTextSize != 0)	{
			message.setTextSize(childTextSize);
		}
		if(childTypeface != null) {
			message.setTypeface(childTypeface);
		}
		
		 final GestureDetector gestureDetector = new GestureDetector(context,new EntryGDetector(Integer.parseInt(info[0])));

		 view.setOnTouchListener(new OnTouchListener() {
			 @Override
			 public boolean onTouch(View v, MotionEvent event) {
				 
				 if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_HOVER_MOVE){
			        	v.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
				 }
				 else if(event.getAction() == MotionEvent.ACTION_DOWN){
				    	v.setBackgroundColor(0xFFFFFFFF);
				 }
				 
				 gestureDetector.onTouchEvent(event);
				 return true;
	            }
	        });
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		DBAdapter database = new DBAdapter(context);
		Cursor entries = database.getEntriesByDay((int)getGroupId(groupPosition));
		int size = entries.getCount();
		database.close();
		entries.close();
		return size;

	}

	@Override
	public Object getGroup(int groupPosition) {	
		return days.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return days.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return Long.parseLong(days.get(groupPosition)[0]);
	}

	@Override
	public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
		String[] info = (String[]) getGroup(groupPosition);
		Boolean hasImage = true;
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.day_style, null);
		}
		
		ImageView imageView = (ImageView) view.findViewById(R.id.dayImage);

		String path = null;
		if (!info[1].equals("")) {
			path = info[1];
		} else {
			hasImage = false;
		}

		bitmapWorker.createTask(imageView, path)
			.setShowDefault(Integer.parseInt(info[0]))
			.setTargetHeight(128)
			.setTargetWidth(128)
			.setCenterCrop(true)
			.setHighQuality(true)
			.setRoundedCorner(15)
			.execute();

		SimpleDateFormat format_in = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
		SimpleDateFormat format_out = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		try {
			Date date = format_in.parse(info[2]);
			((TextView) view.findViewById(R.id.dayDate)).setText(format_out.format(date));
		} catch (ParseException e) {
			//won't happen if we use only dataBaseHelper.addEntry(...)
		}
		
		final GestureDetector gestureDetector = new GestureDetector(context,new DayGDetector(Integer.parseInt(info[0]),hasImage));
		
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
	
	public void setChildFont (Typeface typeface, int textSize){
		childTypeface = typeface;
		childTextSize = textSize;
	}
	

	private class EntryGDetector extends SimpleOnGestureListener {

		private int id;
		private OptionEntryListener activity;
		
		
		public EntryGDetector(int id){
			super();
			this.id = id;		
			try {
				activity =(OptionEntryListener)context;
			} catch (ClassCastException e) {
				// The activity doesn't implement the interface, throw exception
				throw new ClassCastException(context.toString() + " must implement OptionEntryListener");
			}
		}

	    @Override
	    public void onLongPress(MotionEvent event) {
	    	activity.onEntryLongClicked(id);
	    }

	    @Override
	    public boolean onSingleTapConfirmed(MotionEvent event) {
	    	activity.onEntryClicked(id);
	        return true;
	    }
	}
	
	private class DayGDetector extends SimpleOnGestureListener {

		private int id;
		private OptionDayListener activity;
		private boolean longClickEnabled;
		
		
		public DayGDetector(int id, boolean longClickEnabled){
			super();
			this.id = id;
			this.longClickEnabled = longClickEnabled;
			try {
				activity =(OptionDayListener)context;
			} catch (ClassCastException e) {
				// The activity doesn't implement the interface, throw exception
				throw new ClassCastException(context.toString() + " must implement OptionEntryListener");
			}
		}

	    @Override
	    public void onLongPress(MotionEvent event) {
	    	if(longClickEnabled){
	    		activity.onDayLongClicked(id);
	    	}
	    }    
	    
	}
	
	
}
