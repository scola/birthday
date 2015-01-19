package com.google.api.services.samples.calendar.android;

//import com.google.api.services.calendar.model.Calendar;
import android.util.Log;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;

import io.github.scola.birthday.Birthday;
import io.github.scola.birthday.BirthdayLab;
import io.github.scola.birthday.BirthdayListFragment;

import java.io.IOException;
import java.util.List;

public class AsyncBatchDeleteEvent extends CalendarAsyncTask {
	private final String calendar_id;
	private final Birthday birthday;
	private final Boolean update;
	private final static String TAG = "AsyncBatchDeleteEvent";
	
	public AsyncBatchDeleteEvent(BirthdayListFragment calendarSample, String calendar_id, Birthday birthday, Boolean update) {
	    super(calendarSample);
	    this.calendar_id = calendar_id;
	    this.update = update;
	    this.birthday = birthday;
	  }
      
      @Override
      protected void doInBackground() throws IOException {
    	Log.d(TAG, "delete the event" + ": " + birthday);
        BatchRequest batch = client.batch();
        try {
            for (String eventId : birthday.getEventId()) {
              client.events().delete(calendar_id, eventId)
                  .queue(batch, new JsonBatchCallback<Void>(){                	  

                    @Override
                    public void onFailure(GoogleJsonError err, HttpHeaders headers) throws IOException {
                      Utils.logAndShowError(fragment, TAG, err.getMessage());
                    }

					public void onSuccess(Void arg0, HttpHeaders arg1)
							throws IOException {
						Log.d(TAG, "delete one event");
						// TODO Auto-generated method stub
						
					}
                  });
            }
            batch.execute();
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() != 404) {
                throw e;
            }
        }            
            
      }
      
      @Override
      protected final void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        birthday.getEventId().clear();
        if (update) {
        	if(birthday.getIsLunar()) {
        		fragment.createLunarEvent(birthday, false);
        	} else {
        		fragment.createEvent(birthday, false);
        	}
        }
      }
}
