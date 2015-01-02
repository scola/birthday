package io.github.scola.birthday.utils;

import io.github.scola.birthday.Birthday;

import java.util.ArrayList;

public class Util {
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
}
