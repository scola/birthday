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

import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import io.github.scola.birthday.BirthdayListFragment;
import io.github.scola.birthday.R;

/**
 * Common utilities.
 * 
 * @author Yaniv Inbar
 */
public class Utils {

  /**
   * Logs the given throwable and shows an error alert dialog with its message.
   * 
   * @param fragment fragment
   * @param tag log tag to use
   * @param t throwable to log and show
   */
  public static void logAndShow(BirthdayListFragment fragment, String tag, Throwable t) {
    Log.e(tag, "Error", t);
    String message = t.getMessage();
    if (t instanceof GoogleJsonResponseException) {
      GoogleJsonError details = ((GoogleJsonResponseException) t).getDetails();
      if (details != null) {
        message = details.getMessage();
      }
    } else if (t.getCause() instanceof GoogleAuthException) {
      message = ((GoogleAuthException) t.getCause()).getMessage();
    }
    showError(fragment, message);
  }

  /**
   * Logs the given message and shows an error alert dialog with it.
   * 
   * @param fragment fragment
   * @param tag log tag to use
   * @param message message to log and show or {@code null} for none
   */
  public static void logAndShowError(BirthdayListFragment fragment, String tag, String message) {
    String errorMessage = getErrorMessage(fragment, message);
    Log.e(tag, errorMessage);
    showErrorInternal(fragment, errorMessage);
  }

  /**
   * Shows an error alert dialog with the given message.
   * 
   * @param fragment fragment
   * @param message message to show or {@code null} for none
   */
  public static void showError(BirthdayListFragment fragment, String message) {
    String errorMessage = getErrorMessage(fragment, message);
    showErrorInternal(fragment, errorMessage);
  }

  private static void showErrorInternal(final BirthdayListFragment fragment, final String errorMessage) {
    fragment.getActivity().runOnUiThread(new Runnable() {
      public void run() {
        Toast.makeText(fragment.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
      }
    });
  }

  private static String getErrorMessage(BirthdayListFragment fragment, String message) {
    Resources resources = fragment.getResources();
    if (message == null) {
      return resources.getString(R.string.error);
    }
    return resources.getString(R.string.error_format, message);
  }
}
