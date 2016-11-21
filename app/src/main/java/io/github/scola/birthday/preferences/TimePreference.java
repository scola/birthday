package io.github.scola.birthday.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import io.github.scola.birthday.utils.Util;
import io.github.scola.birthday.R;

public class TimePreference extends DialogPreference {
    private int lastHour;
    private int lastMinute;
    private TimePicker picker=null;

    private static final String TAG = "TimePreference";
    
    public static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);      
        
//        setPositiveButtonText(ctxt.getResources().getString(android.R.string.ok));
//        setNegativeButtonText(ctxt.getResources().getString(android.R.string.cancel));
    }

    @Override
    protected View onCreateDialogView() {
        picker=new TimePicker(getContext());

        return(picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setIs24HourView(true);
        
        int[] hour_minute = Util.SplitString(getSummary().toString(), ":");
        lastHour = hour_minute[0];
        lastMinute = hour_minute[1];

        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);        
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        
        Log.d(TAG, "onDialogClosed");

        if (positiveResult) {
            lastHour=picker.getCurrentHour();
            lastMinute=picker.getCurrentMinute();

            String time=String.format("%02d", lastHour) + ":" + String.format("%02d", lastMinute);

            if (callChangeListener(time)) {
                persistString(time);
            }
            setSummary(time);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time=null;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString("00:00");
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            time=defaultValue.toString();
        }

        lastHour=getHour(time);
        lastMinute=getMinute(time);
    }
}