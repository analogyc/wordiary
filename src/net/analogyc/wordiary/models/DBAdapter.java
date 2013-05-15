package net.analogyc.wordiary.models;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {
	
	private DataBaseHelper dbHelper;
	private SQLiteDatabase database;
	
	public DBAdapter(Context contex){
		dbHelper = new DataBaseHelper(contex);
	}
	
	
	//open new writable database if it doesn't exist
	public void open(){
		if (database == null){
			database = dbHelper.getWritableDatabase();
		}
	}
	
	//**Close the database
	public void close() {
	    dbHelper.close();
	    database.close();
	}
	
	//get all the entries in the db 
	public Cursor getAllEntries(){
		String query = "SELECT * FROM " + Entry.TABLE_NAME + " ORDER BY "+Entry.COLUMN_NAME_CREATED+ " DESC";
		return database.rawQuery(query, null);
	}
	
	//get all the entries in the db 
	public Cursor getEntryById(int id){
		String query = "SELECT * FROM " + Entry.TABLE_NAME + " WHERE "+ Entry._ID+ " = "+ id;
		return database.rawQuery(query, null);
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
				mood+  " , " +
				sdf.format(now) +  " )" ;
		database.execSQL(query);
		 
	}
	
	//add new photo
	public void addPhoto( String filename){
		//create the current timestamp
		Date now = new Date(System.currentTimeMillis());
		String DATE_FORMAT = "yyyyMMddHHmmss";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ITALY);
		//insert the entry
		String query = "INSERT INTO " + Day.TABLE_NAME + " ( "+
				Day.COLUMN_NAME_FILENAME + " , " +
				Day.COLUMN_NAME_CREATED + 
				") VALUES ('"+
				filename +  "' , " +
				sdf.format(now) +  " )" ;
		database.execSQL(query);
		 
	}
	
	//get all the entries in the db 
		public Cursor getAllDays(){
			String query = "SELECT * FROM " + Day.TABLE_NAME + " ORDER BY "+Day.COLUMN_NAME_CREATED+ " DESC";
			return database.rawQuery(query, null);
		}
}
