package net.analogyc.wordiary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.analogyc.wordiary.models.DBAdapter;
import net.analogyc.wordiary.models.DataBaseHelper;
import net.analogyc.wordiary.models.EntryAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class EntryActivity extends Activity {
	private int entryId;
	private DBAdapter dataBase;
	private TextView messageText, dateText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);
		
		Intent intent = getIntent();
		
		//normally entryId can't be -1
		entryId = intent.getIntExtra("entryId", -1);
		
		messageText = (TextView) findViewById(R.id.messageText);
		dateText = (TextView) findViewById(R.id.dateText);
		
		dataBase = new DBAdapter(this);
		dataBase.open();
		
		setView();
		
	}
	
	private void setView(){
  		Cursor c = dataBase.getEntryById(entryId);
  		if (!(c.moveToFirst())){
  			//error! wrong ID, but it won't happen
  		}
  		String message = c.getString(2);
  		messageText.setText(message);
  		
  		String d_tmp = c.getString(4);
  		SimpleDateFormat format_in= new SimpleDateFormat("yyyyMMddHHmmss",Locale.ITALY);
  		SimpleDateFormat format_out= new SimpleDateFormat("HH:mm:ss dd/MM/yyyy",Locale.ITALY);
		try {
			Date date = format_in.parse(d_tmp);
			dateText.setText(format_out.format(date)); //probably a better method to do this exists
		} catch (ParseException e) {
			//won't happen if we use only dataBaseHelper.addEntry(...)
		}  
		
		
  		//in the future we will get an image and a mood in the same way		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entry, menu);
		return true;
	}
	
	
	@Override
	protected void onPause(){
		super.onPause();
		dataBase.close();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		dataBase = new DBAdapter(this);
		dataBase.open();
	}

}
