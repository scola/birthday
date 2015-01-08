package io.github.scola.birthday.utils;

import io.github.scola.birthday.Birthday;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class Util {
	private static final String TAG = "Util";
	
	public static int[] SplitString(String summary, String sep) {
		int[] date_time = {0, 0};  
		date_time[0] = Integer.parseInt(summary.split(sep)[0]);
		date_time[1] = Integer.parseInt(summary.split(sep)[1]);
		return date_time;
	}
	
	public static ArrayList<Birthday> cloneList(ArrayList<Birthday> list) {
		ArrayList<Birthday> clone = new ArrayList<Birthday>();
		for (Birthday birthday : list) {
			Birthday birth = new Birthday(birthday);
			clone.add(birth);
		}
	    return clone;
	}
	
	public static Date getFirstDate(String date, String time) {
		Calendar currentCalendar = Calendar.getInstance();
		Calendar remindCalendar = Calendar.getInstance();
		int[] month_day = SplitString(date, "-");
		int year = currentCalendar.get(Calendar.YEAR);
		int[] hour_minute = SplitString(time, ":");
		remindCalendar.set(year, month_day[0] - 1, month_day[1], hour_minute[0], hour_minute[1]);
//		Date retDate = new Date();
		if(remindCalendar.compareTo(currentCalendar) < 0) {
			remindCalendar.set(Calendar.YEAR, year + 1);
//			retDate.setYear(year + 1);
		}
		Log.d(TAG, "current year " + year);
////		retDate.setYear(year);
//		retDate.setMonth(month_day[0]);
//		retDate.setDate(month_day[1]);
//		
//		int[] hour_minute = SplitString(time, ":");
//		retDate.setHours(hour_minute[0]);
//		retDate.setMinutes(hour_minute[1]);
		return remindCalendar.getTime();
	}
	
//	public static int[] SplitMethod(String method) {
//		String
//		int[] date_time = {0, 0};  
////		date_time[0] = Integer.parseInt(summary.split(sep)[0]);
////		date_time[1] = Integer.parseInt(summary.split(sep)[1]);
//		return date_time;
//	}
}
