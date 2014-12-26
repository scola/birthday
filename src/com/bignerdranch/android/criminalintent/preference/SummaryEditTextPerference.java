package com.bignerdranch.android.criminalintent.preference;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class SummaryEditTextPerference extends EditTextPreference{	
	CharSequence mDefaultSummary;
    public SummaryEditTextPerference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
        mDefaultSummary = getSummary();
    }
    
    @Override
    public void setText(String text) {
    	super.setText(text);
    	setSummary(text);
    }
    
    @Override
    public void setSummary(CharSequence summary) {
    	if(summary.toString().isEmpty()) {
    		super.setSummary(mDefaultSummary);
    	} else {
    		super.setSummary(summary);
    	}
    }
}
