package net.analogyc.wordiary.models;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import net.analogyc.wordiary.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class EntryAdapter extends CursorAdapter {

	public EntryAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.entry_style, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String path = cursor.getString(cursor.getColumnIndex(Day.COLUMN_NAME_FILENAME));
		Drawable image = null;
		if (path != null) {
			// credits to http://stackoverflow.com/a/6909144/644504
			// for the solution to "center crop" resize
			Bitmap bmp = BitmapFactory.decodeFile(path);
			if (bmp.getWidth() >= bmp.getHeight()) {
				bmp = Bitmap.createBitmap(
					bmp,
					bmp.getWidth() / 2 - bmp.getHeight() / 2,
					0,
					bmp.getHeight(),
					bmp.getHeight()
				);
			} else {
				bmp = Bitmap.createBitmap(
					bmp,
					0,
					bmp.getHeight() / 2 - bmp.getWidth() / 2,
					bmp.getWidth(),
					bmp.getWidth()
				);
			}

			// set it low, but high enough to work with xxhdpi screens
			Bitmap bmp_resized = Bitmap.createScaledBitmap(bmp, 256, 256, true);
			image = new BitmapDrawable(Resources.getSystem(), bmp_resized);
		} else {
			try {
				image = Drawable.createFromStream(context.getAssets().open("default-avatar.jpg"), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		((ImageView) view.findViewById(R.id.image)).setImageDrawable(image);

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

}
