package io.github.scola.birthday;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.github.scola.birthday.R;
import io.github.scola.birthday.utils.Util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.view.ActionMode;
import android.widget.AbsListView.MultiChoiceModeListener;

public class BirthdayListProviderFragment extends ListFragment {
    private static final String MY_CALENDAR_NAME = "Lunar Birthday";
    private static final String PREF_CALENDAR_ID = "calenderId";
    public static final String PREF_GOOGLE_LOGIN = "googleLogin";
    private static final String GOOGLE_CALENDAR_TYPE = "com.google";

    public static final String TAG = "ProviderFragment";

    private ArrayList<Birthday> mBirthdays;
    private ArrayList<Birthday> mSyncedBirthdays;
    private ArrayList<Birthday> mDeleteBirthdays;

    public static final int REQUEST_NEW_BIRTHDAY = 3;

    private long localCalendarId = -1;
    private boolean mGoogleLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.birthdays_title);
        mBirthdays = BirthdayLab.get(getActivity()).getBirthdays();
        BirthdayAdapter adapter = new BirthdayAdapter(mBirthdays);
        setListAdapter(adapter);
        setRetainInstance(true);

        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        localCalendarId = settings.getLong(PREF_CALENDAR_ID, -1);
        final SharedPreferences sharedPref = getActivity().getSharedPreferences(PREF_GOOGLE_LOGIN, Context.MODE_PRIVATE);
        mGoogleLogin = sharedPref.getBoolean(PREF_GOOGLE_LOGIN, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        Log.d(TAG, "system timeZone: " + TimeZone.getDefault().getID());
        if (isCalendarPermissionGranted(1)) {
            if (localCalendarId == -1) {
                listAllCalendar();
            }
        }

        BirthdayLab.get(getActivity()).sortBirthdayList();
        ((BirthdayAdapter)getListAdapter()).notifyDataSetChanged();

        for(int i = 0; i < mBirthdays.size(); i++) {
            if(localCalendarId == -1 || mBirthdays.get(i).getIsSync() || mBirthdays.get(i).getName().equals(getResources().getString(R.string.summary_name_preference))) {
                continue;
            }
            Log.d(TAG, "Start to sync " + i + ": " + mBirthdays.get(i));
            if(mBirthdays.get(i).getEventId() != null && mBirthdays.get(i).getEventId().size() > 0) {
                //update
                Log.d(TAG, "Start to update " + ": " + mBirthdays.get(i));
                deleteEvent(mBirthdays.get(i));
                if(mBirthdays.get(i).getIsLunar() == false) {
//                    deleteEvent(mBirthdays.get(i));
                    createEvent(mBirthdays.get(i), false);
//                    if(mBirthdays.get(i).getEventId().size() == 1){
//                        //just update
//                        createEvent(mBirthdays.get(i), true);
//                    } else {
//                        //remove event
//                        Log.d(TAG, "Start to delete the exist event and update " + i + ": " + mBirthdays.get(i));
//                        deleteEvent(mBirthdays.get(i));
//                        createEvent(mBirthdays.get(i), false);
//                    }
                } else {
//                    deleteEvent(mBirthdays.get(i));
                    asyncCreateLunarEvent(mBirthdays.get(i));
//                    if(mBirthdays.get(i).getEventId().size() == mBirthdays.get(i).getRepeat()) {
//                        //just update
//                        createLunarEvent(mBirthdays.get(i), true);
//                    } else {
//                        //remove event
//                        Log.d(TAG, "Start to delete the exist event and update " + i + ": " + mBirthdays.get(i));
//                        deleteEvent(mBirthdays.get(i));
//                        createLunarEvent(mBirthdays.get(i), false);
//
//                    }
                }
            } else {
                if(mBirthdays.get(i).getIsLunar()) {
                    asyncCreateLunarEvent(mBirthdays.get(i));
                } else {
                    createEvent(mBirthdays.get(i), false);
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        BirthdayLab.get(getActivity()).saveBirthdays();
    }

//    @Override
//    public void onDetach() {
//    	super.onDetach();
//    	Log.d(TAG, "onDetach()");
//    	getActivity().finish();
//    }

    private void listAllCalendar() {
        String[] projection =
                new String[]{
                        Calendars._ID,
                        Calendars.NAME,
                        Calendars.ACCOUNT_NAME,
                        Calendars.ACCOUNT_TYPE};
        Cursor calCursor =
                getActivity().getContentResolver().
                        query(Calendars.CONTENT_URI,
                                projection,
                                Calendars.VISIBLE + " = 1",
                                null,
                                Calendars._ID + " ASC");
        boolean first = true;
        if (calCursor.moveToFirst()) {
            do {
                long id = calCursor.getLong(0);
                String displayName = calCursor.getString(1);
                Log.d(TAG, "find calendar id: " + id + " Calendars.NAME: " + calCursor.getString(1) + " Calendars.ACCOUNT_NAME:" + calCursor.getString(2) + " Calendars.ACCOUNT_TYPE:" + calCursor.getString(3));
                if (first) {
                    first = false;
                    localCalendarId = calCursor.getLong(0);
                }
                if (calCursor.getString(3).equals(GOOGLE_CALENDAR_TYPE)) {
//                    mGoogleAccount = calCursor.getString(2);
                    mGoogleLogin = true;
                    localCalendarId = calCursor.getLong(0);
                    Log.d(TAG, "mGoogleLogin = true, " + "localCalendarId:" + localCalendarId);
                    break;
                }
                // …
            } while (calCursor.moveToNext());
        }
        calCursor.close();
        Log.d(TAG, "calendar id: " + localCalendarId);
        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(PREF_CALENDAR_ID, localCalendarId);
//        editor.putBoolean(PREF_GOOGLE_LOGIN, mGoogleLogin);
        editor.commit();

        final SharedPreferences sharedPref = getActivity().getSharedPreferences(PREF_GOOGLE_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPref.edit();
        sharedEditor.putBoolean(PREF_GOOGLE_LOGIN, mGoogleLogin);
        sharedEditor.commit();
    }

//    private void loadLocalCalendarAndCreate() {
//        long calId = getCalendarId();
//        if (calId == -1) {
//            Log.d(TAG, "have not create calendar and create now");
//            ContentValues values = new ContentValues();
//            Uri.Builder builder =
//                    Calendars.CONTENT_URI.buildUpon();
//            if (isGoogleAccountLogin) {
//                values.put(
//                        Calendars.ACCOUNT_NAME,
//                        mGoogleAccount);
//                values.put(
//                        Calendars.ACCOUNT_TYPE,
//                        GOOGLE_CALENDAR_TYPE);
//                builder.appendQueryParameter(
//                        Calendars.ACCOUNT_NAME,
//                        mGoogleAccount);
//                builder.appendQueryParameter(
//                        Calendars.ACCOUNT_TYPE,
//                        GOOGLE_CALENDAR_TYPE);
//            } else {
//                values.put(
//                        Calendars.ACCOUNT_NAME,
//                        MY_CALENDAR_NAME);
//                values.put(
//                        Calendars.ACCOUNT_TYPE,
//                        CalendarContract.ACCOUNT_TYPE_LOCAL);
//                builder.appendQueryParameter(
//                        Calendars.ACCOUNT_NAME,
//                        MY_CALENDAR_NAME);
//                builder.appendQueryParameter(
//                        Calendars.ACCOUNT_TYPE,
//                        CalendarContract.ACCOUNT_TYPE_LOCAL);
//            }
//            values.put(
//                    Calendars.NAME,
//                    MY_CALENDAR_NAME);
//            values.put(
//                    Calendars.CALENDAR_DISPLAY_NAME,
//                    MY_CALENDAR_NAME);
//            values.put(
//                    Calendars.CALENDAR_COLOR,
//                    0xffff0000);
//            values.put(
//                    Calendars.CALENDAR_ACCESS_LEVEL,
//                    Calendars.CAL_ACCESS_OWNER);
//            values.put(
//                    Calendars.CALENDAR_TIME_ZONE,
//                    TimeZone.getDefault().getID());
//            values.put(
//                    Calendars.SYNC_EVENTS,
//                    1);
//            values.put(
//                    Calendars.VISIBLE,
//                    1);
//
//            builder.appendQueryParameter(
//                    CalendarContract.CALLER_IS_SYNCADAPTER,
//                    "true");
//            Uri uri =
//                    getActivity().getContentResolver().insert(builder.build(), values);
//            localCalendarId = new Long(uri.getLastPathSegment());
//        } else {
//            Log.d(TAG, "calendar have created");
//            localCalendarId = calId;
//        }
//
//        Log.d(TAG, "calendar id: " + localCalendarId);
//        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putLong(PREF_CALENDAR_ID, localCalendarId);
//        editor.commit();
//    }

    private long getCalendarId() {
        String[] projection = new String[]{Calendars._ID};
        String selection =
                Calendars.NAME +
                        " = ? AND " +
                Calendars.ACCOUNT_TYPE +
                        " = ? ";
        // use the same values as above:
        String[] selArgs =
                new String[]{
                        MY_CALENDAR_NAME,
                        GOOGLE_CALENDAR_TYPE};
        Cursor cursor =
                getActivity().getContentResolver().
                        query(
                                Calendars.CONTENT_URI,
                                projection,
                                selection,
                                selArgs,
                                null);
        long calId = -1;
        if (cursor.moveToFirst()) {
            calId = cursor.getLong(0);
        }
        cursor.close();
        return calId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);

//        getActivity().setProgressBarIndeterminateVisibility(true);

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
                    BirthdayAdapter adapter = (BirthdayAdapter)getListAdapter();
                    int selectCount = 0;
                    for (int i = adapter.getCount() - 1; i >= 0; i--) {
                        if (getListView().isItemChecked(i)) {
                            selectCount++;
                        }
                    }
                    String titleText = String.format(getStringFromRes(R.string.select_items), selectCount);
                    mode.setTitle(titleText);
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_birthday:
//                            if(mDeleteBirthdays != null && mDeleteBirthdays.size() > 0) {
//                                Toast.makeText(getActivity(), getStringFromRes(R.string.wait_for_delete_finish),
//                                        Toast.LENGTH_SHORT).show();
//                                return true;
//                            }
//                            if(numAsyncTasks > 0) {
//                                Toast.makeText(getActivity(), getStringFromRes(R.string.wait_for_sync_finish),
//                                        Toast.LENGTH_SHORT).show();
//                                return true;
//                            }
                            BirthdayAdapter adapter = (BirthdayAdapter)getListAdapter();
//                            BirthdayLab birthdayLab = BirthdayLab.get(getActivity());
                            mDeleteBirthdays = new ArrayList<Birthday>();
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                if (getListView().isItemChecked(i)) {
                                    mDeleteBirthdays.add(adapter.getItem(i));
                                }
                            }
                            deleteItemAndPopAlert();
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

    private void deleteItemAndPopAlert() {
        new AlertDialog.Builder(getActivity())
//        .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.confirm_for_delete)
                .setMessage(R.string.delete_alert_msg)
                .setPositiveButton(R.string.alert_yes_delete, new DialogInterface.OnClickListener() {
                    //            @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mDeleteBirthdays == null || mDeleteBirthdays.size() == 0) return;
                        asyncDeleteEvent();
                    }

                })
                .setNegativeButton(R.string.alert_no_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mDeleteBirthdays.clear();
                    }
                })
                .show();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if(mDeleteBirthdays != null && mDeleteBirthdays.size() > 0) {
            Toast.makeText(getActivity(), getStringFromRes(R.string.wait_for_delete_finish),
                    Toast.LENGTH_SHORT).show();
            return;
        }
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
        Log.d(TAG, "onActivityResult");
        switch (requestCode) {
            case REQUEST_NEW_BIRTHDAY:
                for(int i = 0; i < mBirthdays.size(); i++) {
                    if(mSyncedBirthdays != null && i < mSyncedBirthdays.size() && mSyncedBirthdays.get(i).equals(mBirthdays.get(i))) {
                        Log.d(TAG, "birthday " + i + " not change " + mBirthdays.get(i));
                        continue;
                    }
                    Log.d(TAG, "birthday " + i + " changed");
                    mBirthdays.get(i).setIsSync(false);
                }

                break;
        }
    }

    private void deleteEvent(Birthday birthday) {
        Log.d(TAG, "deleteEvent eventId: " + Arrays.toString(birthday.getEventId().toArray(new String[0])));
//        String[] selArgs =
//                birthday.getEventId().toArray(new String[0]);
//        ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
//
//        int deleted =
//                getActivity().getContentResolver().
//                        delete(
//                                CalendarContract.Events.CONTENT_URI,
//                                CalendarContract.Events._ID + " =?",
//                                selArgs);

        ArrayList<ContentProviderOperation> ops =
                new ArrayList<ContentProviderOperation>();
        for (String eventId : birthday.getEventId()) {
            ops.add(
                    ContentProviderOperation.newDelete(Events.CONTENT_URI)
                            .withSelection(Events._ID + "=?", new String[]{eventId})
                            .build());
        }

        try {
            getActivity().getContentResolver().
                    applyBatch(CalendarContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // do s.th.
            e.printStackTrace();
        }
        birthday.getEventId().clear();
    }

    public void createEvent(Birthday birthday, Boolean update) {
        Log.d(TAG, "createEvent update " + update);
        ContentValues values = new ContentValues();

        setSummary(values, birthday.getName(), birthday.getIsEarly(), birthday.getIsLunar());
        setRecurrence(values, birthday.getIsLunar(), birthday.getRepeat());
//
        Date startDate = Util.getFirstDate(birthday.getDate(), birthday.getTime());
        setStartEndTime(values, startDate);
//        values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PUBLIC);

        if(update) {
//            new AsyncUpdateEvent(this, calendarId, event, birthday).execute();
            String[] selArgs =
                    new String[]{birthday.getEventId().get(0)};
            int updated =
                    getActivity().getContentResolver().
                            update(
                                    CalendarContract.Events.CONTENT_URI,
                                    values,
                                    CalendarContract.Events._ID + " =? ",
                                    selArgs);
            setRemind(values, new Long(selArgs[0]), birthday.getMethod(), birthday.getIsEarly(), update);
        } else {
//            new AsyncInsertEvent(this, calendarId, event, birthday).execute();
            values.put(CalendarContract.Events.CALENDAR_ID, localCalendarId);
            Uri uri = getActivity().getContentResolver().
                    insert(CalendarContract.Events.CONTENT_URI, values);

            long eventId = new Long(uri.getLastPathSegment());

            setRemind(values,eventId, birthday.getMethod(), birthday.getIsEarly(), update);
            birthday.getEventId().add(String.valueOf(eventId));
        }
        birthday.setIsSync(true);
    }

    private void asyncDeleteEvent() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground( Void... voids ) {
                for(Iterator<Birthday> it = mDeleteBirthdays.iterator(); it.hasNext(); ) {
                    Birthday birthday = it.next();
                    if(localCalendarId != -1 && birthday.getEventId().size() > 0) {
                        deleteEvent(birthday);
                    }
                    BirthdayLab.get(getActivity()).deleteBirthday(birthday);
                    it.remove();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void post) {
                super.onPostExecute(post);
                getActivity().setProgressBarIndeterminateVisibility(false);
                ((BirthdayAdapter)getListAdapter()).notifyDataSetChanged();
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                getActivity().setProgressBarIndeterminateVisibility(true);
            }
        }.execute();
    }

    private void asyncCreateLunarEvent(final Birthday birthday) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground( Void... voids ) {
                createLunarEvent(birthday, false);
                return null;
            }
            @Override
            protected void onPostExecute(Void post) {
                super.onPostExecute(post);
                getActivity().setProgressBarIndeterminateVisibility(false);
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                getActivity().setProgressBarIndeterminateVisibility(true);
            }
        }.execute();
    }

    public void createLunarEvent(Birthday birthday, Boolean update) {
        Log.d(TAG, "createLunarEvent update " + update);
        List<Date> startDate = Util.getFirstLunarDate(birthday.getDate(), birthday.getTime(), birthday.getRepeat());
        int i = 0;
        ArrayList<ContentProviderOperation> ops =
                new ArrayList<ContentProviderOperation>();
        for(Date date : startDate) {
            ContentValues values = new ContentValues();
            setSummary(values, birthday.getName(), birthday.getIsEarly(), birthday.getIsLunar());
            setStartEndTime(values, date);
            if(update) {
                String[] selArgs =
                        new String[]{birthday.getEventId().get(i)};
//                int updated =
//                        getActivity().getContentResolver().
//                                update(
//                                        CalendarContract.Events.CONTENT_URI,
//                                        values,
//                                        CalendarContract.Events._ID + " =? ",
//                                        selArgs);

                ops.add(
                        ContentProviderOperation.newUpdate(Events.CONTENT_URI)
                                .withValues(values)
                                .withSelection(Events._ID + "=?", selArgs)
                                .build());
            } else {
                values.put(CalendarContract.Events.CALENDAR_ID, localCalendarId);
//                Uri uri = getActivity().getContentResolver().
//                        insert(CalendarContract.Events.CONTENT_URI, values);

                ops.add(
                        ContentProviderOperation.newInsert(Events.CONTENT_URI)
                                .withValues(values)
                                .build());

//                long eventId = new Long(uri.getLastPathSegment());

//                setRemind(values,eventId, birthday.getMethod(), birthday.getIsEarly());
//                birthday.getEventId().add(String.valueOf(eventId));
            }
            i++;
        }
        ContentProviderResult[] lunarEvent = null;
        try {
            lunarEvent = getActivity().getContentResolver().
                    applyBatch(CalendarContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // do s.th.
            e.printStackTrace();
        }
        ops.clear();

        if (lunarEvent != null) {
            if (update) {
                for (i = 0; i < birthday.getEventId().size(); i++) {
                    ContentValues values = new ContentValues();
                    ops.addAll(setLunarRemind(values, new Long(birthday.getEventId().get(i)), birthday.getMethod(), birthday.getIsEarly(), update));
                }
            } else {
                for (ContentProviderResult event : lunarEvent) {
                    ContentValues values = new ContentValues();
                    long eventId = new Long(event.uri.getLastPathSegment());
                    birthday.getEventId().add(String.valueOf(eventId));
                    ops.addAll(setLunarRemind(values, eventId, birthday.getMethod(), birthday.getIsEarly(), update));
                }
            }

            try {
                getActivity().getContentResolver().
                        applyBatch(CalendarContract.AUTHORITY, ops);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                // do s.th.
                e.printStackTrace();
            }
        }
        birthday.setIsSync(true);
        Log.d(TAG, "createLunarEvent update " + update + " finish");
    }

    private void setSummary(ContentValues values, String name, Boolean isEarly, Boolean isLunar) {
        if(isEarly) {
            if (isLunar) {
                values.put(CalendarContract.Events.TITLE, name + getStringFromRes(R.string.event_lunar_summary) + "(" + getStringFromRes(R.string.summary_early_checkbox_preference) + ")");
            } else {
                values.put(CalendarContract.Events.TITLE, name + getStringFromRes(R.string.event_solar_summary) + "(" + getStringFromRes(R.string.summary_early_checkbox_preference) + ")");
            }
        } else {
            if (isLunar) {
                values.put(CalendarContract.Events.TITLE, name + getStringFromRes(R.string.event_lunar_summary));
            } else {
                values.put(CalendarContract.Events.TITLE, name + getStringFromRes(R.string.event_solar_summary));
            }
        }
    }

    private void setRecurrence(ContentValues values, Boolean isLunar, int repeat) {
        if(isLunar == false && repeat > 1) {
            values.put(CalendarContract.Events.RRULE, "FREQ=YEARLY;COUNT=" + repeat);

        }
    }

    private void setStartEndTime(ContentValues values, Date startDate) {
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        values.put(CalendarContract.Events.DTSTART, startDate.getTime());
        values.put(CalendarContract.Events.DTEND, startDate.getTime() + 3600000);
    }

    private void setRemind(ContentValues values, long eventId, String remindMethod, Boolean isEarly, Boolean update) {
        String[] method = remindMethod.toLowerCase().split(",");
        for(int i = 0; i < method.length; i++) {
            Log.d(TAG, "getMethod " + method[i]);
            values.clear();

            if (method[i].trim().equals("popup")) {
                values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            } else {
                values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_EMAIL);
            }

            if(isEarly) {
                values.put(CalendarContract.Reminders.MINUTES, 60 * 24);
            } else {
                values.put(CalendarContract.Reminders.MINUTES, 10);
            }
            if (update) {
                getActivity().getContentResolver().
                        update(
                                CalendarContract.Reminders.CONTENT_URI,
                                values,
                                CalendarContract.Reminders._ID + " =? ",
                                new String[]{String.valueOf(eventId)});
            } else {
                values.put(CalendarContract.Reminders.EVENT_ID, eventId);
                getActivity().getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, values);
            }
        }
    }

    private ArrayList<ContentProviderOperation> setLunarRemind(ContentValues values, long eventId, String remindMethod, Boolean isEarly, Boolean update) {
        ArrayList<ContentProviderOperation> ops =
                new ArrayList<ContentProviderOperation>();
        String[] method = remindMethod.toLowerCase().split(",");
        for(int i = 0; i < method.length; i++) {
            Log.d(TAG, "getMethod " + method[i]);
            values.clear();
            values.put(CalendarContract.Reminders.EVENT_ID, eventId);

            if (method[i].trim().equals("popup")) {
                values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            } else {
                values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_EMAIL);
            }

            if(isEarly) {
                values.put(CalendarContract.Reminders.MINUTES, 60 * 24);
            } else {
                values.put(CalendarContract.Reminders.MINUTES, 10);
            }

            if (update) {
                ops.add(
                        ContentProviderOperation.newUpdate(CalendarContract.Reminders.CONTENT_URI)
                                .withValues(values)
                                .build());
            } else {
                ops.add(
                        ContentProviderOperation.newInsert(CalendarContract.Reminders.CONTENT_URI)
                                .withValues(values)
                                .build());
            }
        }
        return ops;
    }

    private String getStringFromRes(int id) {
        return getResources().getString(id);
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
                if(mDeleteBirthdays != null && mDeleteBirthdays.size() > 0) {
                    Toast.makeText(getActivity(), getStringFromRes(R.string.wait_for_delete_finish),
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                mSyncedBirthdays = Util.cloneList(mBirthdays);
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

    public  boolean isCalendarPermissionGranted(int request) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_CALENDAR)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR}, request);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"onRequestPermissionsResult Permission: "+ permissions[0] + "was "+ grantResults[0]);
            if (localCalendarId == -1) {
                listAllCalendar();
            }
        } else {
            getActivity().finish();
        }
    }

    public class BirthdayAdapter extends ArrayAdapter<Birthday> {
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
            TextView dayLeftTextView =
                    (TextView)convertView.findViewById(R.id.birthday_list_item_dayLeftTextView);

            long dayLeft = Util.getDayLeft(c.getDate(), c.getIsLunar());
            String dayLeftString = dayLeft + getStringFromRes(R.string.days_left);
            if(dayLeft == 0) {
                dayLeftString = getStringFromRes(R.string.today);
            }

            if (c.getFullDate().length() > 5) {
                int age =Util.getAge(c.getFullDate(), c.getIsLunar());
                String ageText = String.format(getStringFromRes(R.string.age), age);
                dayLeftTextView.setText(dayLeftString + ageText);
            } else {
                dayLeftTextView.setText(dayLeftString);
            }

            String[] weekday = getResources().getStringArray(R.array.week_day_values);
            dateTextView.setText((c.getIsLunar() ? getResources().getString(R.string.lunar) : getResources().getString(R.string.solar)) + " " + c.getDate() + " " + weekday[Util.getWeekday(c.getDate(), c.getIsLunar()) - 1]);

            return convertView;
        }
    }
}

