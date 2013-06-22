package net.analogyc.wordiary.models;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Random;

public class DataBaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "wordiary.db";
	
	public static final int DATABASE_VERSION = 1;
		
	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
	
	public void fillWithFake(SQLiteDatabase db) {
		for (int i = 1; i <= 31; i++){
			String day = "";
			if (i < 10){ 
				day = "0" + i;
			} else {
				day = "" + i;
				
			}
			db.execSQL("INSERT INTO " + Day.TABLE_NAME + " (" + Day.COLUMN_NAME_FILENAME + ", " + Day.COLUMN_NAME_CREATED +  ") VALUES (\"\", \"201305" + day + "000000\")");
		}
		
		Cursor days = db.rawQuery("SELECT "+ Day._ID + ", " + Day.COLUMN_NAME_CREATED + " FROM " + Day.TABLE_NAME, null );
		while (days.moveToNext()){
			db.execSQL("INSERT INTO " + Entry.TABLE_NAME + " (" + Entry._ID + ", " + Entry.COLUMN_NAME_CREATED + ", "
					+ Entry.COLUMN_NAME_MESSAGE + ")  VALUES (?, ?, ?)" , 
					new Object[] {days.getInt(0), days.getString(1), randomString()}) ;
		}
		days.close();
	}
	
	public String randomString(){
		String[] words = {"aliquam", "Proin", "enim", "venenatis", "at", "mi", "diam", "sed", "Curabitur", 
				"vestibulum", "adipiscing", "Lorem", "Aenean", "Aliquam", "mi", "rutrum", "Nullam", "Sed",
				"Phasellus", "convallis", "pulvinar", "pellentesque", "vulputate", "nonummy", "ullamcorper",
				"Quisque", "mollis", "Morbi", "dignissim", "Suspendisse", "rutrum", "lacus", "sagittis",
				"parturient", "ornare", "aptent", "senectus", "auctor", "eget", "Duis"};
		String theWords = "";
		Random rand = new Random();
		int random = rand.nextInt(40);
		
		for (int i = 0; i < 4; i++){
		theWords = theWords + " " + words[rand.nextInt(40)];
		}
		return theWords;
	}
			
	
}
