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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;

import io.github.scola.birthday.Birthday;
import io.github.scola.birthday.BirthdayListFragment;

import java.io.IOException;

/**
 * Asynchronously updates a calendar with a progress dialog.
 * 
 * @author Yaniv Inbar
 */
public class AsyncUpdateEvent extends CalendarAsyncTask {

  private final String calendarId;
  private final Event event;
  private final Birthday birthday;
  
  private final static String TAG = "AsyncUpdateEvent";

  public AsyncUpdateEvent(BirthdayListFragment calendarSample, String calendarId, Event event, Birthday birthday) {
    super(calendarSample);
    this.calendarId = calendarId;
    this.event = event;
    this.birthday = birthday;
  }

  @Override
  protected void doInBackground() throws IOException {
    try {
    	Event updatedEvent = client.events().update(calendarId, birthday.getEventId().get(0), event).execute();
//    	birthday.getEventId().remove(0);
//    	birthday.getEventId().add(updatedEvent.getId());
		birthday.setIsSync(true);
		Log.d(TAG, "update event for " + birthday.getName() + " eventId=" + updatedEvent.getId());
    } catch (GoogleJsonResponseException e) {
      // 404 Not Found would happen if user tries to delete an already deleted calendar
      if (e.getStatusCode() != 404) {
        throw e;
      }
//      model.remove(calendarId);
    }
  }
  
  @Override
  protected final void onPostExecute(Boolean success) {
    super.onPostExecute(success);
    fragment.getSyncingBirthdays().remove(birthday);     
  }
}
