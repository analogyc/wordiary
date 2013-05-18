package net.analogyc.wordiary;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.view.Menu;

public class UserPreferences extends PreferenceActivity{
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_preferences, menu);
		return true;
	}
	
	
}
