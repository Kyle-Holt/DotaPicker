package com.example.kyle.dotapicker;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import static com.example.kyle.dotapicker.MainActivity.bayes_constant;
import static com.example.kyle.dotapicker.MainActivity.setting_changed;
import static com.example.kyle.dotapicker.MainActivity.user_id;

public class PreferencesActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        PreferencesActivity.PrefsFragment mPrefsFragment = new PreferencesActivity.PrefsFragment();
        mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
        mFragmentTransaction.commit();
        saveData();
    }

    void saveData() {
        SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();

        spe.putString("edittext_preference", "default_value");
        spe.commit();

        spe.putString("bayes_preference", "default_value");
        spe.commit();

        SharedPreferences  preferences = PreferenceManager.getDefaultSharedPreferences(this);
        user_id = preferences.getString("edittext_preference", "default_value");

        if(isParsable(preferences.getString("bayes_preference", "default_value"))){
            bayes_constant = Integer.parseInt(preferences.getString("bayes_preference", "default_value"));
        } else {
            bayes_constant = 3;
        }

        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setting_changed = true;

        Intent intent = getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);

        finish();
    }

    public static boolean isParsable(String input){
        boolean parsable = true;
        try{
            Integer.parseInt(input);
        }catch(NumberFormatException e){
            parsable = false;
        }
        return parsable;
    }


    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

            EditTextPreference editTextPref = (EditTextPreference) findPreference("edittext_preference");
            editTextPref.setSummary(editTextPref.getText());
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            Preference pref = findPreference(key);
            if (pref instanceof EditTextPreference) {
                EditTextPreference etp = (EditTextPreference) pref;
                pref.setSummary(etp.getText());
            }
        }

    }
}
