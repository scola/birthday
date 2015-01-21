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

import com.google.api.services.calendar.model.Calendar;

//import io.github.scola.birthday.BirthdayListActivity;
import io.github.scola.birthday.BirthdayListFragment;

import java.io.IOException;

/**
 * Asynchronously insert a new calendar.
 * 
 * @author Yaniv Inbar
 */
public class AsyncInsertCalendar extends CalendarAsyncTask {

  private final Calendar entry;

  public AsyncInsertCalendar(BirthdayListFragment calendarSample, Calendar entry) {
    super(calendarSample);
    this.entry = entry;
  }

  @Override
  protected void doInBackground() throws IOException {
    Calendar calendar = client.calendars().insert(entry).setFields("id,summary").execute();
    SharedPreferences lunarBirthdayCalendarId = fragment.getActivity().getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = lunarBirthdayCalendarId.edit();
    editor.putString(fragment.PREF_GOOGLE_CALENDAR_ID, calendar.getId());
    editor.commit();
    fragment.calendarId = calendar.getId();
//    model.add(calendar);
  }
}
