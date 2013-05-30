package net.analogyc.wordiary.models;


import android.content.Context;
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
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// NOT USED
	}
}
