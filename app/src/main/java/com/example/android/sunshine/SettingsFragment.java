package com.example.android.sunshine;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.sync.SunshineSyncUtils;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);

        // Setting preference summary for each preference that is not CheckBoxPreference (they have
        // their summary set in xml).
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int preferenceCount = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // register Listener to watch for preference changes and set new summary.
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister Listener to prevent memory leaks.
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Sets summary on a preference.
     *
     * @param preference Preference to set summary on.
     * @param value      Summary to set.
     */
    private void setPreferenceSummary(Preference preference, Object value) {
        String valueString = value.toString();
        // For ListPreferences need to find correct entry that corresponds to the value.
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(valueString);
            if (index >= 0) {
                preference.setSummary(listPreference.getEntries()[index]);
            }
        } else {
            preference.setSummary(valueString);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Activity activity = getActivity();

        if (key.equals(getString(R.string.pref_location_key))) {
            // Location changes.
            SunshinePreferences.resetLocationCoordinates(activity);
            SunshineSyncUtils.startImmediateSync(activity);
        } else if (key.equals(getString(R.string.pref_units_key))) {
            // Units have changed. update lists of weather entries accordingly
            activity.getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
        }

        // After every preference change setting summaries again.
        Preference preference = findPreference(key);
        if (preference != null) {
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(key, "");
                setPreferenceSummary(preference, value);
            }
        }
    }
}
