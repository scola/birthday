package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class BirthdayListFragment extends ListFragment {
	
	private static final String TAG = "BirthdayListFragment";
	
    private ArrayList<Birthday> mBirthdays;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.birthdays_title);
        mBirthdays = BirthdayLab.get(getActivity()).getBirthdays();
        BirthdayAdapter adapter = new BirthdayAdapter(mBirthdays);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) { 
        Birthday c = (Birthday)(getListAdapter()).getItem(position);
        //Log.d(TAG, c.getTitle() + " was clicked");
    }

    private class BirthdayAdapter extends ArrayAdapter<Birthday> {
        public BirthdayAdapter(ArrayList<Birthday> Birthdays) {
            super(getActivity(), android.R.layout.simple_list_item_1, Birthdays);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // if we weren't given a view, inflate one
            if (null == convertView) {
                convertView = getActivity().getLayoutInflater()
                    .inflate(R.layout.list_item_birthday, null);
            }

            // configure the view for this Birthday
            Birthday c = getItem(position);

            TextView titleTextView =
                (TextView)convertView.findViewById(R.id.birthday_list_item_nameTextView);
            titleTextView.setText(c.getName());
            TextView dateTextView =
                (TextView)convertView.findViewById(R.id.birthday_list_item_dateTextView);
            dateTextView.setText(c.getDate().toString());

            return convertView;
        }
    }
    
}

