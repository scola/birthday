package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;

import android.content.Context;

public class BirthdayLab {
    private ArrayList<Birthday> mBirthdays;

    private static BirthdayLab sBirthdayLab;
    private Context mAppContext;

    private BirthdayLab(Context appContext) {
        mAppContext = appContext;
        mBirthdays = new ArrayList<Birthday>();
        for (int i = 0; i < 100; i++) {
            Birthday c = new Birthday();
            c.setName("Name #" + (i + 1));
            c.setDate("07-" + (i + 1)); // every other one
            mBirthdays.add(c);
        }
    }

    public static BirthdayLab get(Context c) {
        if (sBirthdayLab == null) {
            sBirthdayLab = new BirthdayLab(c.getApplicationContext());
        }
        return sBirthdayLab;
    }

    public ArrayList<Birthday> getBirthdays() {
        return mBirthdays;
    }
}

