package com.google.api.services.samples.calendar.android;

//import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;

import io.github.scola.birthday.BirthdayListFragment;

import java.io.IOException;

public class AsyncInsertEvent extends CalendarAsyncTask {
	private final String calendar_id;
	private final Event event;
	public AsyncInsertEvent(BirthdayListFragment calendarSample, String calendar_id, Event event) {
	    super(calendarSample);
	    this.calendar_id = calendar_id;
	    this.event = event;
	  }
	
	  @Override
	  protected void doInBackground() throws IOException {
		  Event createEvent = client.events().insert(calendar_id, event).execute();
	    //model.add(event);
	  }
}
