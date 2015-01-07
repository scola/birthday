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
	public static final String TAG = "AsyncLoadCalendars";

  AsyncLoadCalendars(BirthdayListFragment calendarSample) {
    super(calendarSample);
  }

  @Override
  protected void doInBackground() throws IOException {
    CalendarList feed = client.calendarList().list().setFields(CalendarInfo.FEED_FIELDS).execute();
    for (CalendarListEntry calendar : feed.getItems()) {
    	if(calendar.getSummary().equals("Lunar Birthday") && fragment.calendarId == null) {
    		Log.d(TAG, "Lunar Birthday calendar already exist");
    		fragment.calendarId = calendar.getId();
    	}
    }
    
    if(fragment.calendarId == null) {
    	fragment.createNewCalendar();
    }
//    model.reset(feed.getItems());
  }

  public static void run(BirthdayListFragment calendarSample) {
    new AsyncLoadCalendars(calendarSample).execute();
  }
}
