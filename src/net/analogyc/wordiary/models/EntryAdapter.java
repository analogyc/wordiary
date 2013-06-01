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

	private final int THUMBNAIL_SIZE = 256;

	private LruCache<String, Bitmap> mMemoryCache;

	public EntryAdapter(Context context, Cursor c) {
		super(context, c);

		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 4;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
			}
		};
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	class BitmapDrawableWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private final String path;

		public BitmapDrawableWorkerTask(ImageView imageView, String path) {
			// Use a WeakReference to ensure the ImageView can be garbage collected
			imageViewReference = new WeakReference<ImageView>(imageView);
			this.path = path;
		}

		// Resize image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			Bitmap image = getBitmapFromMemCache("models.EntryAdapter.thumbnails." + path);

			if (image != null) {
				return image;
			}

			// get the image width and height without loading it in memory
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bmp = BitmapFactory.decodeFile(path, options);
			int height = options.outHeight;
			int width = options.outWidth;

			// reduce the amount of data allocated in memory with higher inSampleSize
			options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			if (height > THUMBNAIL_SIZE || width > THUMBNAIL_SIZE) {
				final int heightRatio = Math.round((float) height / (float) THUMBNAIL_SIZE);
				final int widthRatio = Math.round((float) width / (float) THUMBNAIL_SIZE);
				options.inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			}
			bmp = BitmapFactory.decodeFile(path, options);

			// center crop so it's square and pretty
			Bitmap bmp_crop;
			// credits to http://stackoverflow.com/a/6909144/644504
			// for the solution to "center crop" resize
			if (bmp.getWidth() >= bmp.getHeight()) {
				bmp_crop = Bitmap.createBitmap(
					bmp,
					bmp.getWidth() / 2 - bmp.getHeight() / 2,
					0,
					bmp.getHeight(),
					bmp.getHeight()
				);
			} else {
				bmp_crop = Bitmap.createBitmap(
					bmp,
					0,
					bmp.getHeight() / 2 - bmp.getWidth() / 2,
					bmp.getWidth(),
					bmp.getWidth()
				);
			}

			// set it low, but high enough to work with xxhdpi screens
			return bmp_crop;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (imageViewReference != null && bitmap != null) {
				addBitmapToMemoryCache("models.EntryAdapter.thumbnails." + path, bitmap);
				final ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

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
