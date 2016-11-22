package io.github.scola.birthday;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;

//import com.google.api.services.samples.calendar.android.CalendarModel;

public class BirthdayListActivity extends SingleFragmentActivity {
	
//	public static final String TAG = "BirthdayListFragment";
//	
//	public static final int REQUEST_AUTHORIZATION = 1;
//
//	public int numAsyncTasks;
//	public CalendarModel model = new CalendarModel();
//	public com.google.api.services.calendar.Calendar client;
	
    @Override
    protected Fragment createFragment() {
        return new BirthdayListFragment();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        // Turn it on
//        setProgressBarIndeterminateVisibility(true);
        // And when you want to turn it off
//        setProgressBarIndeterminateVisibility(false);
    }
}
