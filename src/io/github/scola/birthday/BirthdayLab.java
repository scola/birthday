package io.github.scola.birthday;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

public class BirthdayLab {
	
    private static final String TAG = "BirthdayLab";
    private static final String FILENAME = "birthdays.json";
    
    private ArrayList<Birthday> mBirthdays;
    private BirthdayJSONSerializer mSerializer;

    private static BirthdayLab sBirthdayLab;
    private Context mAppContext;
    

    private BirthdayLab(Context appContext) {
        mAppContext = appContext;
        mSerializer = new BirthdayJSONSerializer(mAppContext, FILENAME);

        try {
        	mBirthdays = mSerializer.loadBirthdays();
        } catch (Exception e) {
        	mBirthdays = new ArrayList<Birthday>();
            Log.e(TAG, "Error loading birthdays: ", e);
        }
//        for (int i = 0; i < 100; i++) {
//            Birthday c = new Birthday();
//            c.setName("Name #" + (i + 1));
//            c.setDate("07-" + (i + 1)); // every other one
//            //c.setRepeat(10);
//            mBirthdays.add(c);
//        }
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
    
    public void addBirthday(Birthday c) {
        mBirthdays.add(c);
        saveBirthdays();
    }
    
    public void deleteBirthday(Birthday c) {
        mBirthdays.remove(c);
        saveBirthdays();
    }
    
    public boolean saveBirthdays() {
        try {
            mSerializer.saveBirthdays(mBirthdays);
            Log.d(TAG, "birthdays saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving birthdays: " + e);
            return false;
        }
    }

}

