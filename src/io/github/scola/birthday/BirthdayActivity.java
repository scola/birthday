package io.github.scola.birthday;

import java.util.UUID;

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
		UUID birthdayId = (UUID)getIntent()
	            .getSerializableExtra(BirthdayFragment.EXTRA_BIRTHDAY_ID);
		BirthdayFragment mPrefsFragment = BirthdayFragment.newInstance(birthdayId);
		mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
		mFragmentTransaction.commit();
    }
}
