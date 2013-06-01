/*
	THIS CLASS CAN BE DELETED(but it contains useful code)

package net.analogyc.wordiary.models;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import net.analogyc.wordiary.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class EntryAdapter extends CursorAdapter {



	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.entry_style, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String path = cursor.getString(cursor.getColumnIndex(Day.COLUMN_NAME_FILENAME));
		ImageView imageView = (ImageView) view.findViewById(R.id.image);
		Bitmap image = null;

		// set a default picture if an image wasn't already set from cache
		try {
			image = BitmapFactory.decodeStream(context.getAssets().open("default-avatar.jpg"));
			imageView.setImageBitmap(image);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (path != null) {
			BitmapDrawableWorkerTask task = new BitmapDrawableWorkerTask(imageView, path);
			task.execute();
		}

		((TextView) view.findViewById(R.id.message)).setText(
			cursor.getString(cursor.getColumnIndex(Entry.COLUMN_NAME_MESSAGE)));

		SimpleDateFormat format_in = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ITALY);
		SimpleDateFormat format_out = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.ITALY);
		try {
			Date date = format_in.parse(cursor.getString(cursor.getColumnIndex(Entry.COLUMN_NAME_CREATED)));
			((TextView) view.findViewById(R.id.date)).setText(format_out.format(date));
		} catch (ParseException e) {
			//won't happen if we use only dataBaseHelper.addEntry(...)
		}
	}

}*/
