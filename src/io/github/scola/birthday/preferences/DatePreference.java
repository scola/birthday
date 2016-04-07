package io.github.scola.birthday.preferences;

import io.github.scola.birthday.utils.Util;

import java.lang.reflect.Field;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

public class DatePreference extends DialogPreference {
	private int lastYear;
	private int lastMonth;
    private int lastDay;
    private DatePicker picker=null;

    public DatePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);      

//        setPositiveButtonText(ctxt.getResources().getString(android.R.string.ok));
//        setNegativeButtonText(ctxt.getResources().getString(android.R.string.cancel));
    }
    
    @Override
    protected View onCreateDialogView() {
        picker=new DatePicker(getContext());
//        try {
//            Field f[] = picker.getClass().getDeclaredFields();
//            for (Field field : f) {
//            	if (field.getName().equals("mYearSpinner") ||field.getName().equals("mYearPicker")) {
//                    field.setAccessible(true);
//                    Object yearPicker = new Object();
//                    yearPicker = field.get(picker);
//                    ((View) yearPicker).setVisibility(View.GONE);
//                }
//            }
//        } 
//        catch (SecurityException e) {
//            Log.d("ERROR", e.getMessage());
//        } 
//        catch (IllegalArgumentException e) {
//            Log.d("ERROR", e.getMessage());
//        } 
//        catch (IllegalAccessException e) {
//            Log.d("ERROR", e.getMessage());
//        }
    	
        return(picker);
    }
    
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        getDateBySummary();       
        
        picker.updateDate(lastYear, lastMonth, lastDay);
        picker.setCalendarViewShown(false);
    }
    
    private void getDateBySummary() {
        int[] month_day = Util.SplitDate(getSummary().toString().trim(), "-");
    	lastYear = month_day[0];
    	lastMonth = month_day[1] - 1;
        lastDay = month_day[2];
    }
    

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
        	lastYear=picker.getYear();
        	lastMonth=picker.getMonth();
        	lastDay=picker.getDayOfMonth();

            String date = lastYear + "-" + String.format("%02d", lastMonth + 1) + "-" + String.format("%02d", lastDay);

            setSummary(date);
        }
    }
}
