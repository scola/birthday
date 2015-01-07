package io.github.scola.birthday;

import java.util.ArrayList;
import java.util.Collections;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.services.samples.calendar.android.CalendarModel;

import io.github.scola.birthday.R;
import io.github.scola.birthday.utils.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.accounts.AccountManager;

import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.client.util.DateTime;

public class BirthdayListFragment extends ListFragment {
	
	private static final String PREF_ACCOUNT_NAME = "accountName";
	
	public static final String TAG = "BirthdayListFragment";
	
    private ArrayList<Birthday> mBirthdays;
    private ArrayList<Birthday> mSyncedBirthdays;
    
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
	public static final int REQUEST_AUTHORIZATION = 1;
	public static final int REQUEST_ACCOUNT_PICKER = 2;
	public static final int REQUEST_NEW_BIRTHDAY = 3;

	public int numAsyncTasks;
	public CalendarModel model = new CalendarModel();
	public com.google.api.services.calendar.Calendar client;
	
	final HttpTransport transport = AndroidHttp.newCompatibleTransport();
	
	final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
	
	GoogleAccountCredential credential;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
//        getActivity().requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.birthdays_title);
        mBirthdays = BirthdayLab.get(getActivity()).getBirthdays();
        BirthdayAdapter adapter = new BirthdayAdapter(mBirthdays);
        setListAdapter(adapter);
        setRetainInstance(true);
        
        credential = GoogleAccountCredential.usingOAuth2(getActivity(), Collections.singleton(CalendarScopes.CALENDAR));
        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        // Calendar client
        client = new com.google.api.services.calendar.Calendar.Builder(
            transport, jsonFactory, credential).setApplicationName("Google-CalendarAndroidSample/1.0")
            .build();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	if (checkGooglePlayServicesAvailable()) {
    	      haveGooglePlayServices();
    	}
    }
    
    private void haveGooglePlayServices() {
        // check if there is already an account selected
        if (credential.getSelectedAccountName() == null) {
            // ask user to choose account
            chooseAccount();
        } else {
            // load calendars
//          AsyncLoadCalendars.run(this);
        }
    }
    
    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }
    
    /** Check that Google Play services APK is installed and up to date. */
    private boolean checkGooglePlayServicesAvailable() {
      final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
      if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
        showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        return false;
      }
      return true;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);   
        
        getActivity().setProgressBarIndeterminateVisibility(true);
        
        ListView listView = (ListView)v.findViewById(android.R.id.list);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(listView);
        } else {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
            
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.birthday_list_item_context, menu);
                    return true;
                }
            
                public void onItemCheckedStateChanged(ActionMode mode, int position,
                        long id, boolean checked) {
                }
            
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_birthday:
                            BirthdayAdapter adapter = (BirthdayAdapter)getListAdapter();
                            BirthdayLab birthdayLab = BirthdayLab.get(getActivity());
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                if (getListView().isItemChecked(i)) {
                                    birthdayLab.deleteBirthday(adapter.getItem(i));
                                }
                            }
                            mode.finish(); 
                            adapter.notifyDataSetChanged();
                            return true;
                        default:
                            return false;
                    }
                }
          
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }
                
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
            
        }

        return v;
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) { 
        Birthday c = (Birthday)(getListAdapter()).getItem(position);
        mSyncedBirthdays = Util.cloneList(mBirthdays);
        //mSyncedBirthdays = (ArrayList<Birthday>)mBirthdays.clone();
        Log.d(TAG, "mSyncedBirthdays first " + mSyncedBirthdays.get(0));
        //Log.d(TAG, c.getTitle() + " was clicked");
        // start an instance of birthdayActivity
        Intent i = new Intent(getActivity(), BirthdayPagerActivity.class);
        i.putExtra(BirthdayFragment.EXTRA_BIRTHDAY_ID, c.getId());
        startActivityForResult(i, REQUEST_NEW_BIRTHDAY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {                
        switch (requestCode) {
        case REQUEST_GOOGLE_PLAY_SERVICES:
          if (resultCode == Activity.RESULT_OK) {
            haveGooglePlayServices();
          } else {
            checkGooglePlayServicesAvailable();
          }
          break;
        case REQUEST_AUTHORIZATION:
          if (resultCode == Activity.RESULT_OK) {
//            AsyncLoadCalendars.run(this);
          } else {
            chooseAccount();
          }
          break;
        case REQUEST_ACCOUNT_PICKER:
          if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
            String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
            if (accountName != null) {
              credential.setSelectedAccountName(accountName);
              SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
              SharedPreferences.Editor editor = settings.edit();
              editor.putString(PREF_ACCOUNT_NAME, accountName);
              editor.commit();
//              AsyncLoadCalendars.run(this);
            }
          }
          break;
        case REQUEST_NEW_BIRTHDAY:
          if (resultCode == Activity.RESULT_OK) {
        	  ((BirthdayAdapter)getListAdapter()).notifyDataSetChanged();
              for(int i = 0; i < mBirthdays.size(); i++) {
              	if(mSyncedBirthdays != null && i < mSyncedBirthdays.size() && mSyncedBirthdays.get(i).equals(mBirthdays.get(i))) {
              		Log.d(TAG, "birthday " + i + " not change " + mSyncedBirthdays.get(i));
              		continue;
              	}
              	Log.d(TAG, "birthday " + i + " changed");
              }
          }
          break;
      }
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_birthday_list, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_birthday:
                Birthday birthday = new Birthday();
                BirthdayLab.get(getActivity()).addBirthday(birthday);
                Intent i = new Intent(getActivity(), BirthdayActivity.class);
                i.putExtra(BirthdayFragment.EXTRA_BIRTHDAY_ID, birthday.getId());
                startActivityForResult(i, REQUEST_NEW_BIRTHDAY);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } 
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
            
            dateTextView.setText((c.getIsLunar() ? getResources().getString(R.string.lunar) : getResources().getString(R.string.solar)) + " " + c.getDate());

            return convertView;
        }
    }
    
    public void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        getActivity().runOnUiThread(new Runnable() {
          public void run() {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                connectionStatusCode, getActivity(), REQUEST_GOOGLE_PLAY_SERVICES);
            dialog.show();
          }
        });
    }
    
    public void refreshView() {
    	
    }
    
}

