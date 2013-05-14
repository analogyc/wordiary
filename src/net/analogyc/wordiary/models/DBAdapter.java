package net.analogyc.wordiary.models;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;

public class DBAdapter {
	DataBaseHelper db;
	
	public DBAdapter(Context contex){
		if(db == null){
			db = new DataBaseHelper(contex);
		}
	}
	
	//get all the entries in the db 
	public Cursor getAllEntries(){
		String query = "SELECT * FROM " + Entry.TABLE_NAME + " ORDER BY "+Entry.COLUMN_NAME_CREATED+ " DESC";
		return db.getReadableDatabase().rawQuery(query, null);
	}
	
	//get all the entries in the db 
	public Cursor getEntryById(int id){
		String query = "SELECT * FROM " + Entry.TABLE_NAME + " WHERE "+ Entry._ID+ " = "+ id;
		return db.getReadableDatabase().rawQuery(query, null);
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
		 db.getReadableDatabase().execSQL(query);
		 
	}
	
	public void close(){
		db.close();
	}
}
