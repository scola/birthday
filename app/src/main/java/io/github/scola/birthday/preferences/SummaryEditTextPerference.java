package io.github.scola.birthday.preferences;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.util.Log;

public class SummaryEditTextPerference extends EditTextPreference{	
	
	private static final String TAG = "SummaryEditTextPerference";
	CharSequence mDefaultSummary;
    public SummaryEditTextPerference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
        mDefaultSummary = getSummary();
        //getEditText().setText("fuck");
    }
    
    @Override
    public void setText(String text) {
    	super.setText(text);
    	Log.d(TAG, "setText text: " + text);
    	setSummary(text);
    }
    
    @Override
    public void setSummary(CharSequence summary) {
    	Log.d(TAG, "setSummary Summary: " + summary);
    	if(summary.toString().isEmpty()) {
    		super.setSummary(mDefaultSummary);
    	} else {
    		super.setSummary(summary);   
    		mDefaultSummary = summary;
    	}
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        
        Log.d(TAG, "onDialogClosed mDefaultSummary: " + mDefaultSummary);
        setSummary(mDefaultSummary);
    }
    
    @Override
    public CharSequence getSummary () {
    	Log.d(TAG, "getSummary Summary: " + mDefaultSummary);
    	return mDefaultSummary;
    }
    
    @Override
    public String getText () {
    	return getSummary().toString();
    }
    
}
