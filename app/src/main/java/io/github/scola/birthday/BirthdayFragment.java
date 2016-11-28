package io.github.scola.birthday;

import java.util.UUID;

import io.github.scola.birthday.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BirthdayFragment extends PreferenceFragment 
				implements OnSharedPreferenceChangeListener{
	
	private static final String TAG = "BirthdayFragment";
	
    Birthday mBirthday;
    private static final String KEY_NAME_PREFERENCE = "name";
    private static final String KEY_LUNAR_PREFERENCE = "lunar";
    private static final String KEY_METHOD_PREFERENCE = "method";
    private static final String KEY_REPEAT_PREFERENCE = "repeat";
    private static final String KEY_DATE_PREFERENCE = "date";
    private static final String KEY_TIME_PREFERENCE = "time";
    private static final String KEY_EARLY_PREFERENCE = "early";
    
    //private static final String[] PERFERENCE_KEYS = {"name", "lunar", "method", "repeat", "date", "time", "early"};
    
    public static final String EXTRA_BIRTHDAY_ID = "birthday.BIRTHDAY_ID";
    
    public static BirthdayFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_BIRTHDAY_ID, crimeId);

        BirthdayFragment fragment = new BirthdayFragment();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        Log.d(TAG, "onCreate");
        //mBirthday = new Birthday();
        addPreferencesFromResource(R.xml.preferences);
        UUID birthdayId = (UUID)getArguments().getSerializable(EXTRA_BIRTHDAY_ID);
        mBirthday = BirthdayLab.get(getActivity()).getBirthday(birthdayId);
        Preference preference = findPreference(KEY_NAME_PREFERENCE);
        if(mBirthday.getName() == null) {
        	mBirthday.setName(getResources().getString(R.string.summary_name_preference));
        }

        preference.setSummary(mBirthday.getName());
        
        String dateSummary = mBirthday.getFullDate();
        preference = findPreference(KEY_DATE_PREFERENCE);
        if (dateSummary.length() == 5) {
        	preference.setSummary("1990-" + dateSummary);
        } else {
        	preference.setSummary(dateSummary);
        }
        
        preference = findPreference(KEY_LUNAR_PREFERENCE);
        ((CheckBoxPreference)preference).setChecked(mBirthday.getIsLunar());
        
        preference = findPreference(KEY_TIME_PREFERENCE);
        preference.setSummary(mBirthday.getTime());
        
        preference = findPreference(KEY_EARLY_PREFERENCE);
        ((CheckBoxPreference)preference).setChecked(mBirthday.getIsEarly());
        
        preference = findPreference(KEY_REPEAT_PREFERENCE);
        preference.setSummary(Integer.toString(mBirthday.getRepeat()));
        
        preference = findPreference(KEY_METHOD_PREFERENCE);
        preference.setSummary(mBirthday.getMethod());
        
        
        setHasOptionsMenu(true);
        //preference.getDialog()
        //preference.getEditText().setText("fuck");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        } 
    }  
  
    @Override
    public void onResume(){
        super.onResume();
        // Set up a listener whenever a key changes
        Log.d(TAG, "onResume");
        getPreferenceScreen().getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(this);
        //updatePreference(KEY_EDIT_TEXT_PREFERENCE);
    }
 
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
            .unregisterOnSharedPreferenceChangeListener(this);        

    	Preference preference = findPreference(KEY_NAME_PREFERENCE);
        String summary = preference.getSummary().toString().trim();
        mBirthday.setName(summary);
        
        preference = findPreference(KEY_DATE_PREFERENCE);
        summary = preference.getSummary().toString().trim();
        mBirthday.setDate(summary);
        
        preference = findPreference(KEY_TIME_PREFERENCE);
        summary = preference.getSummary().toString().trim();
        mBirthday.setTime(summary);
        
        preference = findPreference(KEY_REPEAT_PREFERENCE);
        summary = preference.getSummary().toString().trim();
        mBirthday.setRepeat(Integer.parseInt(summary));
        
        preference = findPreference(KEY_METHOD_PREFERENCE);
        summary = preference.getSummary().toString().trim();
        mBirthday.setMethod(summary);
        
        preference = findPreference(KEY_LUNAR_PREFERENCE);
        Boolean isChecked = ((CheckBoxPreference)preference).isChecked();
        mBirthday.setIsLunar(isChecked);
        
        preference = findPreference(KEY_EARLY_PREFERENCE);
        isChecked = ((CheckBoxPreference)preference).isChecked();
        mBirthday.setIsEarly(isChecked);
        
        BirthdayLab.get(getActivity()).saveBirthdays();
    }
	
	//@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        updatePreference(key);
    }
	
	private void updatePreference(String key){
		Log.d(TAG, "Preference changed");
//		if (key.equals(KEY_NAME_PREFERENCE)) {
//			EditTextPreference preference = (EditTextPreference)findPreference(key);
//			String name = preference.getText().toString().trim();
//			Log.d(TAG, "setName when input name: " + name);
//			if(name != null && false == name.equals(getResources().getString(R.string.summary_name_preference))){
//				getActivity().setTitle(name + getResources().getString(R.string.event_summary));
//	        }
//	        else {
//	        	getActivity().setTitle(R.string.birthdays_title);
//	        }
//		} 
        
//        if (preference instanceof CheckBoxPreference){
//        	CheckBoxPreference checkBoxPreference =  (CheckBoxPreference)preference;
//            Boolean isChecked = checkBoxPreference.isChecked();
//            if (key.equals(KEY_LUNAR_PREFERENCE)){
//            	mBirthday.setIsLunar(isChecked);
//            }else{
//            	mBirthday.setIsEarly(isChecked);
//            }
//        } 
//        else {
//        	String summary = (preference).getSummary().toString().trim();        	
//        	if(summary.length() == 0) return;
//    		if (key.equals(KEY_NAME_PREFERENCE)) {
//    			Log.d(TAG, "setName when input name: " + summary);
//    			mBirthday.setName(summary);
//    		}    		
//    		if (key.equals(KEY_DATE_PREFERENCE)) {
//    			mBirthday.setDate(summary);
//    		}
//    		if (key.equals(KEY_TIME_PREFERENCE)) {
//    			mBirthday.setTime(summary);
//    		}
//    		if (key.equals(KEY_REPEAT_PREFERENCE)) {
//    			mBirthday.setRepeat(Integer.parseInt(summary));
//    		}
//    		if (key.equals(KEY_METHOD_PREFERENCE)) {
//    			mBirthday.setMethod(summary);
//    		}
//        }

    }
	
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.birthday_about_page, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            case R.id.menu_item_about:
            	openAbout();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        } 
    }
    
    public static String getMyVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (null == packageInfo.versionName) {
                return "Unknown";
            } else {
                return packageInfo.versionName;
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to get package info" + e);
            return "Unknown";
        }
    }
    
    private void openAbout() {
        WebView web = new WebView(getActivity());
        web.loadUrl(_(R.string.about_page));
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
        });
        new AlertDialog.Builder(getActivity())
                .setTitle(String.format(_(R.string.about_info_title), getMyVersion(getActivity())))
                .setPositiveButton(R.string.about_info_share, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        intent.putExtra(Intent.EXTRA_SUBJECT, _(R.string.share_subject));                       
                        intent.putExtra(Intent.EXTRA_TEXT, _(R.string.share_content));
                        startActivity(Intent.createChooser(intent, _(R.string.share_channel)));
                    }
                })
                .setNegativeButton(R.string.about_info_close, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setView(web)
                .create()
                .show();        
    }
    
    private String _(int id) {
        return getResources().getString(id);
    }
  
}
