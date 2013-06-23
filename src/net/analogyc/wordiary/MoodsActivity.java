package net.analogyc.wordiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import net.analogyc.wordiary.models.MoodsAdapter;

/**
 * Shows the list of moods for return to the previous activity
 */
public class MoodsActivity extends BaseActivity {
	//the number of the available moods
	private int nMoods = 10;
	private String[] moods = new String[nMoods];
	private GridView gridView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mood);
 
		//fill the array that contains the name of the mood
		for (int i= 1; i<= nMoods; i++){
			moods[i-1] = "mood" + i;
		}

		//get and set the gridview that will show the moods on the screen
		gridView = (GridView) findViewById(R.id.moodGrid);
		gridView.setAdapter(new MoodsAdapter(this, moods));

		// returns the mood back to the previous activity
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adpView, View view, int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("moodId", moods[position]);
				setResult(Activity.RESULT_OK,intent);
				finish();
			}
		});
	}
}

