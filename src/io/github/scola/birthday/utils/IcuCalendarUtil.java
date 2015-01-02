package io.github.scola.birthday.utils;

import com.ibm.icu.util.ChineseCalendar;

import java.util.Calendar;

/**
 * Created by ohjongin on 14. 1. 21.
 */
public class IcuCalendarUtil {
    /**
     * Get lunar date with Calendar class from solar date
     *
     * @param year
     * @param month Janunary is '1' although January is '0' at Calendar class
     * @param day
     *
     * @return A new instance of Calendar.
     */
    public static Calendar getLunarCalendar(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return getLunarCalendar(cal);
    }

    /**
     * Get lunar date with Calendar class from solar date
     *
     * @param cal
     *
     * @return A new instance of Calendar.
     */
    public static Calendar getLunarCalendar(Calendar cal) {
        Calendar lunar_cal = Calendar.getInstance();

        LunarCalendar lc = new LunarCalendar();
        ChineseCalendar cc = lc.toLunar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

        int lunar_year = LunarCalendar.getYear(cc);
        int lunar_month = LunarCalendar.getMonth(cc);
        int lunar_day = LunarCalendar.getDay(cc);

        lunar_cal.set(lunar_year, lunar_month - 1, lunar_day);
        return lunar_cal;
    }

    /**
     * Get solar date with Calendar class from lunar date
     *
     * @param year
     * @param month Janunary is '1' although January is '0' at Calendar class
     * @param day
     *
     * @return A new instance of Calendar.
     */
    public static Calendar getCalendarFromLunar(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return getCalendarFromLunar(cal);
    }

    /**
     * Get solar date with Calendar class from lunar date
     *
     * @param cal
     *
     * @return A new instance of Calendar.
     */
    public static Calendar getCalendarFromLunar(Calendar cal) {
        com.ibm.icu.util.Calendar this_year_lunar = (new LunarCalendar()).fromLunar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        Calendar this_year_cal = Calendar.getInstance();
        this_year_cal.set(LunarCalendar.getYear(this_year_lunar), LunarCalendar.getMonth(this_year_lunar) - 1, LunarCalendar.getDay(this_year_lunar));

        return this_year_cal;
    }
}
