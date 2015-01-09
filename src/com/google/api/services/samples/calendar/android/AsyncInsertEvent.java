package com.google.api.services.samples.calendar.android;

//import com.google.api.services.calendar.model.Calendar;
import android.util.Log;

import com.google.api.services.calendar.model.Event;

import io.github.scola.birthday.Birthday;
import io.github.scola.birthday.BirthdayListFragment;

import java.io.IOException;

public class AsyncInsertEvent extends CalendarAsyncTask {
	private final String calendar_id;
	private final Event event;
	private final Birthday birthday;
	private final static String TAG = "AsyncInsertEvent";
	public AsyncInsertEvent(BirthdayListFragment calendarSample, String calendar_id, Event event, Birthday birthday) {
	    super(calendarSample);
	    this.calendar_id = calendar_id;
	    this.event = event;
	    this.birthday = birthday;
	  }
	
	  @Override
	  protected void doInBackground() throws IOException {
		  Event createEvent = client.events().insert(calendar_id, event).execute();
		  birthday.getEventId().add(createEvent.getId());
		  birthday.setIsSync(true);
		  Log.d(TAG, "create event for " + birthday.getName() + " eventId=" + createEvent.getId());
	  }
}
