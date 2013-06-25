package net.analogyc.wordiary.models;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnLongClickListener;
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

	public EntryListAdapter(Context context, BitmapWorker bitmapWorker) {
		this.context = context;
		this.bitmapWorker = bitmapWorker;
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
		Cursor entries = database.getEntryByDay((int)getGroupId(groupPosition));
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
		((TextView) view.findViewById(R.id.entryMessage)).setText(info[1]);
		
		 final GestureDetector gestureDetector = new GestureDetector(context,new GDetector(Integer.parseInt(info[0])));

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
		Cursor entries = database.getEntryByDay((int)getGroupId(groupPosition));
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
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.day_style, null);
		}

		Bitmap image;
		ImageView imageView = (ImageView) view.findViewById(R.id.dayImage);

		// set a default picture if an image wasn't already set from cache
		try {
			image = BitmapFactory.decodeStream(context.getAssets().open("default-avatar.jpg"));
			imageView.setImageBitmap(image);

			if (!info[1].equals("")) {
				bitmapWorker.createTask(imageView, info[1])
					.setDefaultBitmap(image)
					.setTargetHeight(128)
					.setTargetWidth(128)
					.setCenterCrop(true)
					.setHighQuality(true)
					.setRoundedCorner(15)
					.execute();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		SimpleDateFormat format_in = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ITALY);
		SimpleDateFormat format_out = new SimpleDateFormat("dd.MM.yyyy", Locale.ITALY);
		try {
			Date date = format_in.parse(info[2]);
			((TextView) view.findViewById(R.id.dayDate)).setText(format_out.format(date));
		} catch (ParseException e) {
			//won't happen if we use only dataBaseHelper.addEntry(...)
		}
		
		final int groupId = (int) getGroupId(groupPosition);
		
		view.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View view) {
				((OptionDayListener)context).onDayLongClicked(groupId);
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

	private class GDetector extends SimpleOnGestureListener {

		private int id;
		private OptionEntryListener activity;
		
		public GDetector(int id){
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
	
	
}