package com.kodekoan.wereallgonnadie;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Created by halkeye on 5/22/13.
 */
public class SettingsActivity extends PreferenceActivity {
    final static int PICK_AUDIO = 1;

    private static final String TAG = SettingsActivity.class.toString();


    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragment_preferences_main);

            Preference pref=findPreference("sound_file");
            pref.setOnPreferenceClickListener (new Preference.OnPreferenceClickListener(){
                public boolean onPreferenceClick(Preference preference){
                    Intent intent = new Intent();
                    intent.setType("audio/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.pick_audio)), PICK_AUDIO);
                    return true;
                }
            });

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == PICK_AUDIO)
            {
                if( resultCode == RESULT_OK)
                {
                    Uri selectedAudioUri = data.getData();
                    Log.w(TAG, "Hey there:" + selectedAudioUri);
                }
                return;
            }
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();


    }
}