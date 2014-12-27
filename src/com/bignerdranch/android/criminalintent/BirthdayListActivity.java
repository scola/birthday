package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

public class BirthdayListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new BirthdayListFragment();
    }
}
