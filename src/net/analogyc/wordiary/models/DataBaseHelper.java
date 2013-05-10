package net.analogyc.wordiary.models;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
			Entry.COLUMN_NAME_MOOD + " INTEGER," +
			Entry.COLUMN_NAME_CREATED + " INTEGER" +
			");"
		);
		
		db.execSQL(
			"CREATE TABLE " + Day.TABLE_NAME + " (" +
			Day._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			Day.COLUMN_NAME_FILENAME + " TEXT," +
			Day.COLUMN_NAME_CREATED + " INTEGER" +
			");"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// NOT USED
	}
	
	
	//get all the entries in the db 
	public Cursor getAllEntries(){
		String query = "SELECT * FROM " + Entry.TABLE_NAME + " ORDER BY "+Entry.COLUMN_NAME_CREATED+ " DESC";
		return getReadableDatabase().rawQuery(query, null);
	}
	
	//get all the entries in the db 
	public Cursor getEntryById(int id){
		String query = "SELECT * FROM " + Entry.TABLE_NAME + " WHERE "+ Entry._ID+ " = "+ id;
		return getReadableDatabase().rawQuery(query, null);
	}
	
	
	//add new entry
	public void addEntry( String text, int mood){
		//create the current timestamp
		Date now = new Date(System.currentTimeMillis());
		String DATE_FORMAT = "yyyyMMddHHmmss";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ITALY);
		//insert the entry
		String query = "INSERT INTO " + Entry.TABLE_NAME + " ( "+
				Entry.COLUMN_NAME_MESSAGE + " , " +
				Entry.COLUMN_NAME_MOOD + " , " +
				Entry.COLUMN_NAME_CREATED + 
				") VALUES ('"+
				text +  "' , " +
				mood+  " , '" +
				sdf.format(now) +  "' )" ;
		 getReadableDatabase().execSQL(query);
		 
	}
}
