package net.analogyc.wordiary.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import net.analogyc.wordiary.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class EntryListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<String[]> days= new ArrayList<String[]>();

	public EntryListAdapter(Context context) {
		this.context = context;
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


		if (!info[0].equals("")) {
			Drawable image = Drawable.createFromPath(info[1]);
			((ImageView)view.findViewById(R.id.dayImage)).setImageDrawable(image);
		}
		
		
		SimpleDateFormat format_in = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ITALY);
		SimpleDateFormat format_out = new SimpleDateFormat("dd.MM.yyyy", Locale.ITALY);
		try {
			Date date = format_in.parse(info[2]);
			((TextView) view.findViewById(R.id.dayDate)).setText(format_out.format(date));
		} catch (ParseException e) {
			//won't happen if we use only dataBaseHelper.addEntry(...)
		}
		
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



}