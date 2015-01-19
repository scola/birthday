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

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import android.os.AsyncTask;
import android.view.View;

import io.github.scola.birthday.BirthdayLab;
import io.github.scola.birthday.BirthdayListFragment;
import io.github.scola.birthday.R;

import java.io.IOException;

/**
 * Asynchronous task that also takes care of common needs, such as displaying progress,
 * authorization, exception handling, and notifying UI when operation succeeded.
 * 
 * @author Yaniv Inbar
 */
abstract class CalendarAsyncTask extends AsyncTask<Void, Void, Boolean> {

  final BirthdayListFragment fragment;
  final CalendarModel model;
  final com.google.api.services.calendar.Calendar client;
//  private final View progressBar;

  CalendarAsyncTask(BirthdayListFragment fragment) {
    this.fragment = fragment;
    model = fragment.model;
    client = fragment.client;
//    progressBar = fragment.getListView().findViewById(R.id.title_refresh_progress);
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    fragment.numAsyncTasks++;
//    progressBar.setVisibility(View.VISIBLE);
    fragment.getActivity().setProgressBarIndeterminateVisibility(true);
  }

  @Override
  protected final Boolean doInBackground(Void... ignored) {
    try {
      doInBackground();
      return true;
    } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
      fragment.showGooglePlayServicesAvailabilityErrorDialog(
          availabilityException.getConnectionStatusCode());
    } catch (UserRecoverableAuthIOException userRecoverableException) {
      fragment.startActivityForResult(
          userRecoverableException.getIntent(), BirthdayListFragment.REQUEST_AUTHORIZATION);
    } catch (IOException e) {
      Utils.logAndShow(fragment, BirthdayListFragment.TAG, e);
    }
    return false;
  }

  @Override
  protected void onPostExecute(Boolean success) {
    super.onPostExecute(success);
    if (0 == --fragment.numAsyncTasks) {
//      progressBar.setVisibility(View.GONE);
    	if(fragment.getActivity() != null) {
    		fragment.getActivity().setProgressBarIndeterminateVisibility(false); 
    		BirthdayLab.get(fragment.getActivity()).saveBirthdays();    		
    	}    	
    }
    if (success) {
      fragment.refreshView();
    }
  }

  abstract protected void doInBackground() throws IOException;
}
