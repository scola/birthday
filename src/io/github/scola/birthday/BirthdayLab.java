package io.github.scola.birthday;

import java.util.ArrayList;
import java.util.UUID;

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

    public Birthday getBirthday(UUID id) {
        for (Birthday c : mBirthdays) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }
    
    public ArrayList<Birthday> getBirthdays() {
        return mBirthdays;
    }
}

