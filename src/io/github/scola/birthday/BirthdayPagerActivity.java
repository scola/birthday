package io.github.scola.birthday;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;

import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v13.app.FragmentPagerAdapter;

import android.support.v4.view.ViewPager;

public class BirthdayPagerActivity extends FragmentActivity {
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        final ArrayList<Birthday> birthdays = BirthdayLab.get(this).getBirthdays();

        FragmentManager fm = getFragmentManager();
        mViewPager.setAdapter(new FragmentPagerAdapter(fm) {
            @Override
            public int getCount() {
                return birthdays.size();
            }
            @Override
            public Fragment getItem(int pos) {
                UUID birthdayId =  birthdays.get(pos).getId();
                return BirthdayFragment.newInstance(birthdayId);
            }
        }); 
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                // Check if this is the page you want.
//            	String name = birthdays.get(position).getName();
//                if(name != null && false == name.equals(getResources().getString(R.string.summary_name_preference))){
//                	setTitle(name + getResources().getString(R.string.event_summary));
//                }
//                else {
//                	setTitle(R.string.birthdays_title);
//                }
            	setBirthdayTitle(birthdays, position);
            }
        });
        UUID birthdayId = (UUID)getIntent().getSerializableExtra(BirthdayFragment.EXTRA_BIRTHDAY_ID);
        for (int i = 0; i < birthdays.size(); i++) {
            if (birthdays.get(i).getId().equals(birthdayId)) {
                mViewPager.setCurrentItem(i);
                setBirthdayTitle(birthdays, i);
                break;
            } 
        }
    }
    
    private void setBirthdayTitle(ArrayList<Birthday> birthdays, int i) {
    	String name = birthdays.get(i).getName();
        if(name != null && false == name.equals(getResources().getString(R.string.summary_name_preference))){
        	setTitle(name + getResources().getString(R.string.event_summary));
        }
        else {
        	setTitle(R.string.birthdays_title);
        }
    }
}

