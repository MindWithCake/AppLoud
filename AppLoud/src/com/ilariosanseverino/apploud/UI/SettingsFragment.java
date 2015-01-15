package com.ilariosanseverino.apploud.UI;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.ilariosanseverino.apploud.R;

public class SettingsFragment extends PreferenceFragment {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
