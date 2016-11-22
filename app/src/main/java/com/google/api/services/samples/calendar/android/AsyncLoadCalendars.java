/*
 * Copyright (c) 2012 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.services.samples.calendar.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import io.github.scola.birthday.BirthdayListFragment;

import java.io.IOException;

/**
 * Asynchronously load the calendars.
 * 
 * @author Yaniv Inbar
 */
public class AsyncLoadCalendars extends CalendarAsyncTask {
	private static final String TAG = "AsyncLoadCalendars";

  AsyncLoadCalendars(BirthdayListFragment calendarSample) {
    super(calendarSample);
  }

  @Override
  protected void doInBackground() throws IOException {
    CalendarList feed = client.calendarList().list().setFields("items(id,summary,timeZone)").execute();
    String timeZone = "Asia/Shanghai";
    for (CalendarListEntry calendar : feed.getItems()) {
    	Log.d(TAG, "return calendar summary:" + calendar.getSummary() + " timeZone:" + calendar.getTimeZone());
    	if(calendar.getSummary().equals("Lunar Birthday") && fragment.calendarId == null) {
    		Log.d(TAG, "Lunar Birthday calendar already exist:" + calendar.getId());
    		fragment.calendarId = calendar.getId();
    	}
    	if(calendar.getSummary().equals(fragment.getGoogleAccount())) {
    		if(calendar.getTimeZone() != null) timeZone = calendar.getTimeZone();
    		fragment.setTimeZone(timeZone);
    		Log.d(TAG, "get google account timeZone:" + calendar.getTimeZone());
    	}
    }
	SharedPreferences lunarBirthdayCalendarId = fragment.getActivity().getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = lunarBirthdayCalendarId.edit();
    if(fragment.calendarId == null) {
    	fragment.createNewCalendar();
    } else {
	    editor.putString(fragment.PREF_GOOGLE_CALENDAR_ID, fragment.calendarId);	    
    }    
    editor.putString(fragment.PREF_GOOGLE_CALENDAR_TIMEZONE, timeZone);
    editor.commit();
//    model.reset(feed.getItems());
  }

  public static void run(BirthdayListFragment calendarSample) {
    new AsyncLoadCalendars(calendarSample).execute();
  }
}
