package net.analogyc.wordiary;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {

	private ListView mainListView;
	private ArrayAdapter<String> listAdapter;  
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mainListView = (ListView) findViewById(R.id.listView1);
        
        String[] days = new String[] { "Today", "Yesterday", "2 days ago", "3 days ago", 
                "29/03/2013", "28/03/2013", "27/03/2013", "26/03/2013"}; 
        
        ArrayList<String> daysList = new ArrayList<String>();  
        daysList.addAll(Arrays.asList(days)); 
        
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, daysList);
        
        mainListView.setAdapter(listAdapter);
        
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String message = "adasdas";
            	Intent intent = new Intent(MainActivity.this, DayActivity.class);
            	intent.putExtra("m", message);
            	startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
