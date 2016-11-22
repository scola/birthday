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
	
	public static int[] SplitDate(String summary, String sep) {
		int[] date_time = {0, 0, 0};  
		date_time[0] = Integer.parseInt(summary.split(sep)[0]);
		date_time[1] = Integer.parseInt(summary.split(sep)[1]);
		date_time[2] = Integer.parseInt(summary.split(sep)[2]);
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
		remindCalendar.set(year, month_day[0] - 1, month_day[1]);

		if(remindCalendar.compareTo(currentCalendar) < 0) {
			remindCalendar.set(Calendar.YEAR, year + 1);
		}
		Log.d(TAG, "current year " + year);
		remindCalendar.set(Calendar.HOUR_OF_DAY, hour_minute[0]);
		remindCalendar.set(Calendar.MINUTE, hour_minute[1]);
		return remindCalendar.getTime();
	}
	
	public static List<Date> getFirstLunarDate(String date, String time, int repeat) {
		Calendar currentCalendar = Calendar.getInstance();
		
		Solar today = new Solar();
		today.solarYear = currentCalendar.get(Calendar.YEAR);
		today.solarMonth = currentCalendar.get(Calendar.MONTH) + 1;
		today.solarDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
		
		int[] month_day = SplitString(date, "-");
		Lunar lunar = new Lunar();
		lunar.isleap = false;
		lunar.lunarYear = today.solarYear;
		lunar.lunarMonth = month_day[0];
		lunar.lunarDay = month_day[1];
		Solar solar = LunarSolarConverter.LunarToSolar(lunar);
		
		if(compareDate(solar, today) < 0) {
			lunar.lunarYear++;
			solar = LunarSolarConverter.LunarToSolar(lunar);
		} else {
			lunar.lunarYear--;
			Solar lastYear = LunarSolarConverter.LunarToSolar(lunar);
			if(compareDate(lastYear, today) < 0) lunar.lunarYear++;
			else solar = lastYear;
		}
		
		int[] hour_minute = SplitString(time, ":");
		Calendar cal = Calendar.getInstance();
		cal.set(solar.solarYear, solar.solarMonth - 1, solar.solarDay);
		cal.set(Calendar.HOUR_OF_DAY, hour_minute[0]);
		cal.set(Calendar.MINUTE, hour_minute[1]);
		
		List<Date> dates = new ArrayList<Date>();
		dates.add(cal.getTime());
		for(int i = 1; i < repeat; i++) {
			lunar.lunarYear++;
			solar = LunarSolarConverter.LunarToSolar(lunar);
			cal.set(solar.solarYear, solar.solarMonth - 1, solar.solarDay);
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
	
	public static int getAge(String date, Boolean isLunar) {
		int[] birthday = SplitDate(date, "-");
		int year = birthday[0];
		Date birthDate;
		Calendar cal = Calendar.getInstance();
		if(isLunar) {
			Log.d(TAG, "date " + date);
			birthDate = getFirstLunarDate(date.substring(5), "12:00", 1).get(0);			
			cal.setTime(birthDate);
			Solar solar_birthday = new Solar();
			solar_birthday.solarYear = cal.get(Calendar.YEAR);
			solar_birthday.solarMonth = cal.get(Calendar.MONTH)+ 1;
			solar_birthday.solarDay = cal.get(Calendar.DAY_OF_MONTH);
			Log.d(TAG, "solar_birthday.solarYear " + solar_birthday.solarYear + " solar_birthday.solarMonth " + solar_birthday.solarMonth + " solar_birthday.solarDay " + solar_birthday.solarDay);
			Lunar lunar = LunarSolarConverter.SolarToLunar(solar_birthday);
			return lunar.lunarYear - year;
		} else {
			birthDate = getFirstDate(date.substring(5), "12:00");
			cal.setTime(birthDate);
			return cal.get(Calendar.YEAR) - year;
		}
	}
	
	public static int compareDate(Solar solar, Solar today) {
		if(solar.solarYear < today.solarYear) return -1;
		if(solar.solarYear > today.solarYear || solar.solarMonth > today.solarMonth) return 1;
		if(solar.solarMonth == today.solarMonth && solar.solarDay > today.solarDay) return 1;
		if(solar.solarYear == today.solarYear && solar.solarMonth == today.solarMonth && solar.solarDay == today.solarDay)			
			return 0;
		return -1;
	}
}
