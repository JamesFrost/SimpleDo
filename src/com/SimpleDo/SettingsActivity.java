package com.SimpleDo;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Settings activity.
 *
 * @author James Frost
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
