package net.analogyc.wordiary;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

/**
 * Displays the preferences: font size, font family, grace period
 */
public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        // grace_period must be a number and must be below 168
        findPreference("grace_period").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        int value;

                        try {
                            value = Integer.parseInt((String) newValue);
                            // if the number starts with 0 and is higher than 0, or the value is higher than 168
                            if (!newValue.toString().matches("^([1-9][0-9]*)|([0])$") || (value > 168)) {
                                // throw the number format exception as the parseInt would
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException e) {
                            Toast toast1 = Toast.makeText(getBaseContext(), getString(R.string.accepted_values), 1000);
                            toast1.show();
                            return false;
                        }

                        return true;
                    }
                });
    }
}
