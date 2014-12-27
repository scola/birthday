package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.app.FragmentManager;

public class BirthdayActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		// Display the fragment as the main content.
		FragmentManager mFragmentManager = getFragmentManager();
		FragmentTransaction mFragmentTransaction = mFragmentManager
				.beginTransaction();
		BirthdayFragment mPrefsFragment = new BirthdayFragment();
		mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
		mFragmentTransaction.commit();
    }
}
