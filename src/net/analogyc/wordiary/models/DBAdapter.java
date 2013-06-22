package net.analogyc.wordiary.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBAdapter {

	private DataBaseHelper dbHelper;
	private SQLiteDatabase database;
	private SharedPreferences preferences;

	/**
	 * Constructor
	 * <p/>
	 * You must call open() on this object to use other methods
	 */
	public DBAdapter(Context context) {
		dbHelper = new DataBaseHelper(context);
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}


	/**
	 * Returns an open, writable database, or creates a new instance
	 */
	private SQLiteDatabase getConnection() {
		if (database == null) {
			database = dbHelper.getWritableDatabase();
		}

		return database;
	}

	/**
	 * Close databaseHelper
	 */
	public void close() {
		if (database != null) {
			database.close();
			database = null;
		}
	}

	/**
	 * Get all the entries in the db
	 *
	 * @return Cursor that contains all entries ordered by date
	 */
	public Cursor getAllEntries() {
		String query = "SELECT * FROM " + Entry.TABLE_NAME + " ORDER BY " + Entry.COLUMN_NAME_CREATED + " DESC";
		return getConnection().rawQuery(query, null);
	}

	/**
	 * Get all the entries in the db
	 *
	 * @return Cursor that contains all entries ordered by date
	 */
	public Cursor getAllEntriesWithImage() {
		String query = "SELECT " + Day.TABLE_NAME + "." + Day.COLUMN_NAME_FILENAME + ", " +
				Entry.TABLE_NAME + "." + Entry.COLUMN_NAME_MESSAGE + ", " +
				Entry.TABLE_NAME + "." + Entry._ID + ", " +
				Entry.TABLE_NAME + "." + Entry.COLUMN_NAME_CREATED +
				" FROM " + Entry.TABLE_NAME + " LEFT OUTER JOIN " + Day.TABLE_NAME +
				" ON " + Entry.TABLE_NAME + "." + Entry.COLUMN_NAME_DAY_ID +
				" = " +
				Day.TABLE_NAME + "." + Day._ID +
				" ORDER BY " + Entry.TABLE_NAME + "." + Entry.COLUMN_NAME_CREATED + " DESC";
		return getConnection().rawQuery(query, null);
	}

	/**
	 * Get the selected entry
	 *
	 * @param id entry's id
	 * @return a Cursor that contains the selected entry, or null
	 */
	public Cursor getEntryById(int id) {
		String query = "SELECT * FROM " + Entry.TABLE_NAME + " WHERE " + Entry._ID + " = " + id;
		return getConnection().rawQuery(query, null);
	}
	
	/**
	 * Get the selected entry
	 *
	 * @param id entry's id
	 * @return a Cursor that contains the selected entry, or null
	 */
	public Cursor getEntryByDay(int id) {
		String query = "SELECT * FROM " + Entry.TABLE_NAME +
						" WHERE " + Entry.COLUMN_NAME_DAY_ID + " = " + id +
						" ORDER BY "+ Entry._ID+" DESC";
		Log.w(null,query);
		return getConnection().rawQuery(query, null);
	}


	/**
	 * Add a new entry
	 *
	 * @param text the message of the entry
	 * @param mood the correspondent mood
	 */
	public void addEntry(String text, int mood) {
		//create the current timestamp
		Date now = new Date(System.currentTimeMillis());
		String DATE_FORMAT = "yyyyMMddHHmmss";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ITALY);
		String query =
				"SELECT * " +
						"FROM " + Day.TABLE_NAME +
						" WHERE " + Day.COLUMN_NAME_CREATED + " LIKE '" + sdf.format(now).substring(0, 8) + "%'";

		Cursor c = getConnection().rawQuery(query, null);
		int photo;
		if (c.moveToFirst()) {
			photo = c.getInt(0);
		} else {
			addPhoto("");
			query =
				"SELECT * " +
					"FROM " + Day.TABLE_NAME +
					" WHERE " + Day.COLUMN_NAME_CREATED + " LIKE '" + sdf.format(now).substring(0, 8) + "%'";

			c = getConnection().rawQuery(query, null);
			c.moveToFirst();
			photo = c.getInt(0);

		}
		c.close();
		//insert the entry
		query = "INSERT INTO " + Entry.TABLE_NAME + " ( " +
				Entry.COLUMN_NAME_MESSAGE + " , " +
				Entry.COLUMN_NAME_MOOD + " , " +
				Entry.COLUMN_NAME_DAY_ID + " , " +
				Entry.COLUMN_NAME_CREATED +
				") VALUES ( ?,?,?,? )";
		getConnection().execSQL(query, new Object[]{text, mood, photo, sdf.format(now)});
	}

	/**
	 * Delete a entry
	 *
	 * @param id the message id
	 */
	public void deleteEntry(int id) {
		//delete the entry
		String query = "DELETE FROM " + Entry.TABLE_NAME + " WHERE " + Entry._ID + " = " + id;
		getConnection().execSQL(query);
	}


	/**
	 * Add a new photo
	 *
	 * @param filename the path of the photo
	 */
	public void addPhoto(String filename) {
		//create the current timestamp
		Date now = new Date(System.currentTimeMillis());
		String DATE_FORMAT = "yyyyMMddHHmmss";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ITALY);
		String date = sdf.format(now);

		//get the id of the day
		String query =
			"SELECT * " +
				" FROM " + Day.TABLE_NAME +
				" WHERE " + Day.COLUMN_NAME_CREATED + " LIKE '" + date.substring(0, 8) + "%'";

		Cursor c = getConnection().rawQuery(query, null);

		if(c.getCount() > 0) {
			c.moveToFirst();
			query = "UPDATE " + Day.TABLE_NAME + " " +
				"SET " + Day.COLUMN_NAME_FILENAME + " = ?" +
				"WHERE " + Day._ID + " = ?";
			getConnection().execSQL(query, new Object[] {filename, c.getInt(0)});
		} else {
			//insert the entry
			query = "INSERT INTO " + Day.TABLE_NAME + " ( " +
				Day.COLUMN_NAME_FILENAME + " , " +
				Day.COLUMN_NAME_CREATED +
				") VALUES (?, ?)";
			getConnection().execSQL(query, new Object[] {filename, date});
		}
	}

	/**
	 * Get a photo by inserting the
	 *
	 * @param day Day in format yyyyMMdd
	 * @return The database row, one or none
	 */
	public Cursor getPhotoByDay(String day) {
		String query =
			"SELECT * " +
			"FROM " + Day.TABLE_NAME + " " +
			"WHERE " + Day.COLUMN_NAME_CREATED + " LIKE '" + day + "%'";

		return getConnection().rawQuery(query, null);
	}


	/**
	 * Get all the days ordered by date (DESC)
	 *
	 * @return Cursor containing the days
	 */
	public Cursor getAllDays() {
		String query = "SELECT * FROM " + Day.TABLE_NAME + " ORDER BY " + Day._ID + " DESC";
		return getConnection().rawQuery(query, null);
	}
	
	/**
	 * Get all the days ordered by date (DESC)
	 *
	 * @return Cursor containing the days
	 */
	public Cursor getAllPhotos() {
		String query = "SELECT * FROM " + Day.TABLE_NAME + 
					" WHERE " + Day.COLUMN_NAME_FILENAME + "<> ''" +
					" ORDER BY " + Day._ID + " DESC";
		return getConnection().rawQuery(query, null);
	}

	/**
	 * Get the selected entry
	 *
	 * @param id entry's id
	 * @return a Cursor that contains the selected entry, or null
	 */
	public Cursor getDayById(int id) {
		String query = "SELECT * FROM " + Day.TABLE_NAME + " WHERE " + Day._ID + " = " + id;
		return getConnection().rawQuery(query, null);
	}

	/**
	 * Modify the mood of the selected entry
	 *
	 * @param id entry's id
	 * @param mood name
	 */
	public void updateMood(int entryId, String moodId) {
		String query = "UPDATE " + Entry.TABLE_NAME +
			" SET "+ Entry.COLUMN_NAME_MOOD +" =  ? WHERE " + Entry._ID + " = ?";
		getConnection().execSQL(query, new Object[]{moodId,entryId});
	}
	
	
	/**
	 * Modify the message of the selected entry
	 *
	 * @param id entry's id
	 * @param message
	 */
	public void updateMessage(int entryId, String message) {
		String query = "UPDATE " + Entry.TABLE_NAME +
			" SET "+ Entry.COLUMN_NAME_MESSAGE +" =  ? WHERE " + Entry._ID + " = ?";
		getConnection().execSQL(query, new Object[]{message,entryId});
	}
	
	/**
	 * Verify if the selected entry can be modified
	 *
	 * @param int entry id
	 * @throws ParseException 
	 * @return boolean true if is editable, false otherwise
	 */
	public boolean isEditable(int entryId){
		 int grace_period = Integer.parseInt(preferences.getString("grace_period", "1"));
		//create the current timestamp
		Date now = new Date(System.currentTimeMillis());
		String DATE_FORMAT = "yyyyMMddHHmmss";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ITALY);

		String query = "SELECT * FROM " + Entry.TABLE_NAME + " WHERE " + Entry._ID + " = " + entryId;
		Cursor c =getConnection().rawQuery(query, null);
		c.moveToFirst();
		Date created;
		try {
			created = sdf.parse(c.getString(4));
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		c.close();

		long now_mil = now.getTime();
		long created_mil = created.getTime();
		
		long diff = now_mil - created_mil;
		return diff < grace_period * 60 *60 * 1000;

	}

	
	
}
