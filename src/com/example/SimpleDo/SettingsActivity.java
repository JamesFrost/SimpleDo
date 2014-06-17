package com.example.SimpleDo;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author James Frost
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
