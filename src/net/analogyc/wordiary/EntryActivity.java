package net.analogyc.wordiary;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class EntryActivity extends Activity {
	private int entryId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);
		
		Intent intent = getIntent();
		
		//normally entryId can't be -1
		entryId = intent.getIntExtra("entryId", -1);
		
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText(""+entryId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entry, menu);
		return true;
	}

}
