package net.analogyc.wordiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import net.analogyc.wordiary.adapters.MoodsAdapter;

/**
 * Shows the list of moods for return to the previous activity
 */
public class MoodsActivity extends BaseActivity {
	//the number of the available moods
	private int mNumberMoods = 10;
	private String[] mMoods = new String[mNumberMoods];
	private GridView mGridView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mood);
 
		//fill the array that contains the name of the mood
		for (int i= 1; i<= mNumberMoods; i++){
			mMoods[i-1] = "mood" + i;
		}

		//get and set the gridview that will show the moods on the screen
		mGridView = (GridView) findViewById(R.id.moodGrid);
		mGridView.setAdapter(new MoodsAdapter(this, mMoods));

		// returns the mood back to the previous activity
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adpView, View view, int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("moodId", mMoods[position]);
				setResult(Activity.RESULT_OK,intent);
				finish();
			}
		});
	}
}

