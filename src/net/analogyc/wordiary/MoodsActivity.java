package net.analogyc.wordiary;

import java.io.IOException;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MoodsActivity extends Activity {
	private String[] moods;
	private final String PATH = "smiles";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mood);
		
		try {
			moods = getAssets().list(PATH);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LinearLayout mylayout = new LinearLayout(this);
		Bitmap image;
		ImageButton ibutton;
		for (int i = 0; i< moods.length; i++){
			try {
				image =BitmapFactory.decodeStream((getAssets().open(PATH + "/" + moods[i])));
				image = Bitmap.createScaledBitmap(image, 32, 32, false);
				ibutton = new ImageButton(this);
				ibutton.setImageBitmap(image);
				mylayout.addView(ibutton);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setContentView(mylayout);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mood, menu);
		return true;
	}

}
