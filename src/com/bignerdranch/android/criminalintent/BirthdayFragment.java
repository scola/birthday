package com.bignerdranch.android.criminalintent;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class BirthdayFragment extends PreferenceFragment 
				implements OnSharedPreferenceChangeListener{
    Birthday mBirthday;
    private static final String KEY_NAME_PREFERENCE = "name";
    private static final String KEY_LUNAR_PREFERENCE = "lunar";
    private static final String KEY_METHOD_PREFERENCE = "method";
    private static final String KEY_REPEAT_PREFERENCE = "repeat";
    private static final String KEY_DATE_PREFERENCE = "date";
    private static final String KEY_TIME_PREFERENCE = "time";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBirthday = new Birthday();
        addPreferencesFromResource(R.xml.preferences);
    }
    
    @Override
    public void onResume(){
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(this);
        //updatePreference(KEY_EDIT_TEXT_PREFERENCE);
    }
 
    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
            .unregisterOnSharedPreferenceChangeListener(this);
    }
	
	//@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        updatePreference(key);
    }
	
	private void updatePreference(String key){
        Preference preference = findPreference(key);
        if (preference instanceof CheckBoxPreference){
        	CheckBoxPreference checkBoxPreference =  (CheckBoxPreference)preference;
            Boolean isChecked = checkBoxPreference.isChecked();
            if (key.equals(KEY_LUNAR_PREFERENCE)){
            	mBirthday.setIsLunar(isChecked);
            }else{
            	mBirthday.setIsEarly(isChecked);
            }
        } else {
        	String summary = preference.getSummary().toString().trim();        	
        	if(summary.length() == 0) return;
    		if (key.equals(KEY_NAME_PREFERENCE)) {
    			mBirthday.setName(summary);
    		}    		
    		if (key.equals(KEY_DATE_PREFERENCE)) {
    			mBirthday.setDate(summary);
    		}
    		if (key.equals(KEY_TIME_PREFERENCE)) {
    			mBirthday.setTime(summary);
    		}
    		if (key.equals(KEY_REPEAT_PREFERENCE)) {
    			mBirthday.setRepeat(Integer.parseInt(summary));
    		}
    		if (key.equals(KEY_METHOD_PREFERENCE)) {
    			mBirthday.setMethod(summary);
    		}
        }

    }
    
/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.xml.preferences, parent, false);
        
        return v; 
    }
*/    
}
