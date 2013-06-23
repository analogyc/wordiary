package net.analogyc.wordiary.models;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.util.Random;

public class DataBaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "wordiary.db";
	public static final int DATABASE_VERSION = 1;
	private Context context;
		
	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
			"CREATE TABLE " + Entry.TABLE_NAME + " (" +
				Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				Entry.COLUMN_NAME_DAY_ID + " INTEGER," +
				Entry.COLUMN_NAME_MESSAGE + " TEXT," +
				Entry.COLUMN_NAME_MOOD + " TEXT," +
				Entry.COLUMN_NAME_CREATED + " TEXT" +
			");"
		);
		
		db.execSQL(
			"CREATE TABLE " + Day.TABLE_NAME + " (" +
				Day._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				Day.COLUMN_NAME_FILENAME + " TEXT," +
				Day.COLUMN_NAME_CREATED + " TEXT" +
			");"
		);

		fillWithFake(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// NOT USED
	}

	/**
	 * Fills the database with random data
	 *
	 * @param db
	 */
	public void fillWithFake(SQLiteDatabase db) {
		for (int i = 1; i <= 31; i++) {
			String day;

			if (i < 10) {
				day = "0" + i;
			} else {
				day = "" + i;
				
			}
		 	
			db.execSQL("INSERT INTO " + Day.TABLE_NAME + " " +
				"(" + Day.COLUMN_NAME_FILENAME + ", " + Day.COLUMN_NAME_CREATED +  ") " +
				"VALUES (?, ?)", new String[] {randomImage(), "201305" + day + "000000"});
		}
		

		Cursor days = db.rawQuery("SELECT "+ Day._ID + ", " + Day.COLUMN_NAME_CREATED + " FROM " + Day.TABLE_NAME, null);

		while (days.moveToNext()) {
			Random rand = new Random();
			for (int i = 0; i < rand.nextInt(15); i++){
			db.execSQL("INSERT INTO " + Entry.TABLE_NAME + " " +
				"(" + Entry.COLUMN_NAME_DAY_ID + ", " + Entry.COLUMN_NAME_CREATED + ", "
				+ Entry.COLUMN_NAME_MESSAGE + ")  VALUES (?, ?, ?)" ,
				new String[] {days.getString(0), days.getString(1), randomString()});

			}
		}

		days.close();
	}

	/**
	 * Returns a random string as example for the entries
	 *
	 * @return Example string
	 */
	public String randomString(){
		String[] words = {"aliquam", "Proin", "enim", "venenatis", "at", "mi", "diam", "sed", "Curabitur", 
				"vestibulum", "adipiscing", "Lorem", "Aenean", "Aliquam", "mi", "rutrum", "Nullam", "Sed",
				"Phasellus", "convallis", "pulvinar", "pellentesque", "vulputate", "nonummy", "ullamcorper",
				"Quisque", "mollis", "Morbi", "dignissim", "Suspendisse", "rutrum", "lacus", "sagittis",
				"parturient", "ornare", "aptent", "senectus", "auctor", "eget", "Duis"};

		String theWords = "";
		Random rand = new Random();

		for (int i = 0; i < rand.nextInt(14)+1; i++){
			theWords = theWords + " " + words[rand.nextInt(40)];
		}

		theWords = theWords.substring(1);
		return theWords;
	}

	/**
	 * Returns the URI to a random image from assets
	 *
	 * @return The uri of the image
	 */
	public String randomImage() {
		Random rand = new Random();
		if (rand.nextInt(3) == 0){
			return "";
		}
		String[] images = null;
		try {
			images = context.getAssets().list("testing_images");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String image = images[rand.nextInt(images.length)];
		
		try
		{
			InputStream in = context.getAssets().open("testing_images/" + image);
			File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
				+ "/MyCameraApp/");
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) 
					+ "/MyCameraApp/" + image);

			if (! dir.exists()){
				if (! dir.mkdirs()){
					Log.d("MyCameraApp", "failed to create directory");
					throw new RuntimeException();
				}
			}

			OutputStream out = new FileOutputStream(file);

			byte[] buffer = new byte[1024];
			int read;
			while((read = in.read(buffer)) != -1){
				out.write(buffer, 0, read);
			}

			in.close();
			out.close();
			return file.getPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";

	}
			
	
}
