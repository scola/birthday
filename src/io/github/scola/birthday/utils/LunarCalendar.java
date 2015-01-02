package io.github.scola.birthday.utils;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ChineseCalendar;

/**
 * Created by ohjongin on 14. 1. 10.
 */
public class LunarCalendar {
    private Calendar mSolarCal;
    private ChineseCalendar mLunarCal;

    public LunarCalendar() {
        mSolarCal = Calendar.getInstance();
        mLunarCal = new ChineseCalendar();
    }

    /**
     * 양력(yyyyMMdd) -> 음력(yyyyMMdd)
     *
     */
    public ChineseCalendar toLunar(int year, int month, int day) {
        mSolarCal.set(Calendar.YEAR, year);
        mSolarCal.set(Calendar.MONTH, month - 1);
        mSolarCal.set(Calendar.DAY_OF_MONTH, day);

        mLunarCal.setTimeInMillis(mSolarCal.getTimeInMillis());

        return mLunarCal;
    }

    /**
     * 음력(yyyyMMdd) -> 양력(yyyyMMdd)
     *
     */
    public Calendar fromLunar(int year, int month, int day) {
        mLunarCal.set(ChineseCalendar.EXTENDED_YEAR, year + 2637);
        mLunarCal.set(ChineseCalendar.MONTH, month - 1);
        mLunarCal.set(ChineseCalendar.DAY_OF_MONTH, day);

        mSolarCal.setTimeInMillis(mLunarCal.getTimeInMillis());

        return mSolarCal;
    }

    public ChineseCalendar getChineseCalendar() {
        return mLunarCal;
    }

    public Calendar getCalendar() {
        return mSolarCal;
    }

    public static int getYear(ChineseCalendar cc) {
        return cc.get(ChineseCalendar.EXTENDED_YEAR) - 2637;
    }

    public static int getMonth(ChineseCalendar cc) {
        return cc.get(ChineseCalendar.MONTH) + 1;
    }

    public static int getDay(ChineseCalendar cc) {
        return cc.get(ChineseCalendar.DAY_OF_MONTH);
    }

    public static int getYear(Calendar cal) {
        return cal.get(Calendar.YEAR);
    }

    public static int getMonth(Calendar cal) {
        return cal.get(Calendar.MONTH) + 1;
    }

    public static int getDay(Calendar cal) {
        return cal.get(Calendar.DAY_OF_MONTH);
    }

}
