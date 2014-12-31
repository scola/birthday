package io.github.scola.birthday.utils;

public class Util {
	public static int[] SplitString(String summary, String sep) {
		int[] date_time = {0, 0};  
		date_time[0] = Integer.parseInt(summary.split(sep)[0]);
		date_time[1] = Integer.parseInt(summary.split(sep)[1]);
		return date_time;
	}
}
