package net.analogyc.wordiary.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.analogyc.wordiary.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class EntryAdapter extends CursorAdapter{
	
	public EntryAdapter(Context context, Cursor c)
	{
		super(context, c);
	}
 
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		return LayoutInflater.from(context).inflate(R.layout.entry_style, null);
	}
 
	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{
		
		//((ImageView) view.findViewById(R.id.image)).setImageDrawable(drawable)(
		//		cursor.getString(cursor.getColumnIndex(Entry.COLUMN_NAME_MESSAGE)));
		
		((TextView) view.findViewById(R.id.message)).setText(
			cursor.getString(cursor.getColumnIndex(Entry.COLUMN_NAME_MESSAGE)));
		
  		SimpleDateFormat format_in= new SimpleDateFormat("yyyyMMddHHmmss",Locale.ITALY);
  		SimpleDateFormat format_out= new SimpleDateFormat("HH:mm:ss dd/MM/yyyy",Locale.ITALY);
		try {
			Date date = format_in.parse(cursor.getString(cursor.getColumnIndex(Entry.COLUMN_NAME_CREATED)));
			((TextView) view.findViewById(R.id.date)).setText(format_out.format(date));
		} catch (ParseException e) {
			//won't happen if we use only dataBaseHelper.addEntry(...)
		}  
		
	}

}
