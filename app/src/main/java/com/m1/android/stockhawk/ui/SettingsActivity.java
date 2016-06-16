package com.m1.android.stockhawk.ui;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.m1.android.stockhawk.R;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_layout);
        PreferenceSummary(findPreference("language"));

    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue=newValue.toString();
        if(preference instanceof ListPreference)
        {
            ListPreference listPreference=(ListPreference)preference;
            int pref=listPreference.findIndexOfValue(stringValue);
            if(pref>0)
            {
                preference.setSummary(listPreference.getEntries()[pref]);
            }
            else
            {
                preference.setSummary(stringValue);
            }
        }
        return true;
    }
    private void PreferenceSummary(Preference preference)
    {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences
                (preference.getContext()).getString(preference.getKey(), ""));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public Intent getParentActivityIntent()
    {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
