package io.github.scola.birthday.utils;

import io.github.scola.birthday.Birthday;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
		if(list == null || list.size() == 0) return null;
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

		if(remindCalendar.compareTo(currentCalendar) < 0) {
			remindCalendar.set(Calendar.YEAR, year + 1);
		}
		Log.d(TAG, "current year " + year);
		return remindCalendar.getTime();
	}
	
	public static List<Date> getFirstLunarDate(String date, String time, int repeat) {
		Calendar currentCalendar = Calendar.getInstance();
		int year = currentCalendar.get(Calendar.YEAR);
		int[] month_day = SplitString(date, "-");
		Calendar cal = IcuCalendarUtil.getCalendarFromLunar(year, month_day[0], month_day[1]);
		if(cal.compareTo(currentCalendar) < 0) {
			cal = IcuCalendarUtil.getCalendarFromLunar(++year, month_day[0], month_day[1]);
		} else {
			Calendar lastYear = IcuCalendarUtil.getCalendarFromLunar(year - 1, month_day[0], month_day[1]);
			if(lastYear.compareTo(currentCalendar) >= 0) {
				cal = lastYear;
				year--;
			}
		}
		int[] hour_minute = SplitString(time, ":");
		cal.set(Calendar.HOUR_OF_DAY, hour_minute[0]);
		cal.set(Calendar.MINUTE, hour_minute[1]);
		
		List<Date> dates = new ArrayList<Date>();
		dates.add(cal.getTime());
		for(int i = 1; i < repeat; i++) {
			cal = IcuCalendarUtil.getCalendarFromLunar(++year, month_day[0], month_day[1]);
			cal.set(Calendar.HOUR_OF_DAY, hour_minute[0]);
			cal.set(Calendar.MINUTE, hour_minute[1]);
			dates.add(cal.getTime());
		}		
		
		return dates;
	}
	
	public static long getDayLeft(String date, Boolean isLunar) {
		Date birthDate;
		if(isLunar) {
			birthDate = getFirstLunarDate(date, "12:00", 1).get(0);
		} else {
			birthDate = getFirstDate(date, "12:00");
		}
		Calendar cal = Calendar.getInstance();
		Calendar currentCalendar = Calendar.getInstance();
		cal.setTime(birthDate);
		long birthdayTime = cal.getTimeInMillis();
		cal.set(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
		long todayTime = cal.getTimeInMillis();
		return (birthdayTime - todayTime)/ (24 * 3600 * 1000);
	}
}
