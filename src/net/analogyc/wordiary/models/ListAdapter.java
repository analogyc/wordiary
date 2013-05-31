package net.analogyc.wordiary.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import net.analogyc.wordiary.R;
import net.analogyc.wordiary.models.EntryAdapter.BitmapDrawableWorkerTask;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private Cursor days;

	public ListAdapter(Context context) {
		this.context = context;
		DBAdapter database = new DBAdapter(context);
		days = database.getAllDays();
		database.close();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 1;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, 
			View view, ViewGroup parent) {

		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.entry_style, null);
		}

		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {

		return 1;

	}

	@Override
	public Object getGroup(int groupPosition) {
		days.move(groupPosition);
		String[] day = {days.getString(1), days.getString(2)};
		return day;
	}

	@Override
	public int getGroupCount() {
		return days.getCount();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
		String[] info = (String[]) getGroup(groupPosition);
		
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.day_style, null);
		}


		if (info[1] != null) {
			BitmapDrawableWorkerTask task = new BitmapDrawableWorkerTask(imageView, path);
			((ImageView)view.findViewById(R.id.image)).setImageBitmap(bitmap);
		}

		SimpleDateFormat format_in = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ITALY);
		SimpleDateFormat format_out = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.ITALY);
		try {
			Date date = format_in.parse(cursor.getString(cursor.getColumnIndex(Entry.COLUMN_NAME_CREATED)));
			((TextView) view.findViewById(R.id.date)).setText(format_out.format(date));
		} catch (ParseException e) {
			//won't happen if we use only dataBaseHelper.addEntry(...)
		}
		
		image = Drawable.createFromPath(path);
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