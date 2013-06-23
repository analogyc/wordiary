package net.analogyc.wordiary;



import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences); 
		
		findPreference("grace_period").setOnPreferenceChangeListener(
			new Preference.OnPreferenceChangeListener() {
				
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					int value;
					
					try {
						value = Integer.parseInt((String) newValue);
						if (!newValue.toString().matches("^([1-9][0-9]*)|([0])$") || (value > 168)) {
							throw new NumberFormatException();
						}
					}
					catch(NumberFormatException e) {
						Toast toast1 = Toast.makeText(getBaseContext(), getString(R.string.accepted_values), 1000);
						toast1.show();
						return false;
					}

					return true;
				}
			});
	}
}
