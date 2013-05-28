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
	
	/**
	 * Constructor
	 * 
	 * You must call open() on this object to use other methods
	 * */
	public DBAdapter(Context contex){
		dbHelper = new DataBaseHelper(contex);
	}
	
	
	/**
	 * Open a new writable database
	 * */
	public void open(){
		if (database == null){
			database = dbHelper.getWritableDatabase();
		}
	}
	
	/**
	 * Close databaseHelper
	 * */
	public void close() {
	    dbHelper.close();
	    database.close();
	}
	
	/**
	 * Get all the entries in the db 
	 * 
	 * @return Cursor that contains all entries ordered by date
	 */
	public Cursor getAllEntries(){
		String query = "SELECT * FROM " + Entry.TABLE_NAME + " ORDER BY "+Entry.COLUMN_NAME_CREATED+ " DESC";
		return database.rawQuery(query, null);
	}
	
	/**
	 * Get the selected entry 
	 * 
	 * @param id entry's id
	 * @return a Cursor that contains the selected entry, or null
	 */
	public Cursor getEntryById(int id){
		String query = "SELECT * FROM " + Entry.TABLE_NAME + " WHERE "+ Entry._ID+ " = "+ id;
		return database.rawQuery(query, null);
	}
	
	
	/**
	 * Add a new entry
	 * 
	 * @param text the message of the entry
	 * @param mood the correspondent mood
	 */
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
				") VALUES ( ?,?,? )" ;
		database.execSQL(query, new Object[] {text,mood,sdf.format(now)});
	}
	
	/**
	 * Delete a entry
	 * 
	 * @param id the message id
	 * 
	 */
	public void deleteEntry( int id){
		//delete the entry
		String query = "DELETE FROM " + Entry.TABLE_NAME + " WHERE "+ Entry._ID+ " = "+ id;
		database.execSQL(query);
	}
	
	
	
	
	/**
	 * Add a new photo
	 * 
	 * @param filename the path of the photo
	 */
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

    /**
     * Get a photo by inserting the
     *
     * @param day Day in format yyyyMMdd
     *
     * @return The database row, one or none
     */
    public Cursor getPhotoByDay(String day) {
        String query =
                "SELECT * " +
                "FROM "+ Day.TABLE_NAME + " " +
                "WHERE " + Day.COLUMN_NAME_CREATED + " LIKE '" + day + "%'";

        return database.rawQuery(query, null);
    }

	
	/**
	 * Get all the days ordered by date (DESC) 
	 * 
	 * @return Cursor containing the days
	 */
    public Cursor getAllDays(){
        String query = "SELECT * FROM " + Day.TABLE_NAME + " ORDER BY "+Day.COLUMN_NAME_CREATED+ " DESC";
        return database.rawQuery(query, null);
    }
}
