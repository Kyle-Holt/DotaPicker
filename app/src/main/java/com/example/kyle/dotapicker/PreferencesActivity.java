package com.example.kyle.dotapicker;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static com.example.kyle.dotapicker.MainActivity.setting_changed;
import static com.example.kyle.dotapicker.MainActivity.user_id;

public class PreferencesActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    static String user_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        getSupportActionBar().setTitle("Settings");

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

        /*
        spe.putString("bayes_preference", "default_value");
        spe.commit();
        */

        SharedPreferences  preferences = PreferenceManager.getDefaultSharedPreferences(this);
        user_id = preferences.getString("edittext_preference", "default_value");

        /*
        if(isParsable(preferences.getString("bayes_preference", "default_value"))){
            bayes_constant = Integer.parseInt(preferences.getString("bayes_preference", "default_value"));
        } else {
            bayes_constant = 3;
        }
        */

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
           if(isParsable(editTextPref.getText())) {
                //setChosenHeroWinRates(editTextPref.getText());
                editTextPref.setSummary(editTextPref.getText());
            }

            Preference myPref = (Preference) findPreference("about");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto","kyle.holt387@gmail.com", null));
                    //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    //intent.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                    return true;
                }
            });
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

    public static void setChosenHeroWinRates(String user_id) {
        JsonChosenTask jsonTask = new JsonChosenTask();
        jsonTask.execute("https://api.opendota.com/api/players/" + user_id + "/");
    }

    public static class JsonChosenTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    //Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                JSONObject jsonArray = new JSONObject((buffer.toString()));
                String str = jsonArray.getString("profile");
                List<String> userStr = Arrays.asList(str.split(","));
                str = userStr.get(1);
                userStr = Arrays.asList(str.split(":"));
                str = userStr.get(1);
                str = str.replace("\"", "");
                Log.d("jsonARRAY", str);
                jsonArray.getString("profile");
                user_name = str;

                return str;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            user_name = result;
            //getHeroWinRates(result);
            //if (pd.isShowing()){
            //    pd.dismiss();
            //}
            //  txtJson.setText(result);
        }
    }


}
