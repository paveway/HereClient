package info.paveway.hereclient;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsPreferenceActivity extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_settings);
    }
}
