package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Birthday {
	private UUID mId;
	private String mName;
	private String mDate;
	private String mTime;
	private Boolean isLunar;
	private Boolean isEarly;
	private int mRepeat;
	private String mMethod;
	private List<String> mEventId;
	
    public Birthday() {
        mId = UUID.randomUUID();
        isLunar = true;
        isEarly = false;
        mRepeat = 10;
        mMethod = "Email";
        mEventId = new ArrayList<String>();
    }

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String date) {
		mDate = date;
	}

	public String getTime() {
		return mTime;
	}

	public void setTime(String time) {
		mTime = time;
	}

	public Boolean getIsLunar() {
		return isLunar;
	}

	public void setIsLunar(Boolean isLunar) {
		this.isLunar = isLunar;
	}

	public Boolean getIsEarly() {
		return isEarly;
	}

	public void setIsEarly(Boolean isEarly) {
		this.isEarly = isEarly;
	}

	public int getRepeat() {
		return mRepeat;
	}

	public void setRepeat(int repeat) {
		mRepeat = repeat;
	}

	public String getMethod() {
		return mMethod;
	}

	public void setMethod(String method) {
		mMethod = method;
	}

	public List<String> getEventId() {
		return mEventId;
	}

	public void setEventId(List<String> eventId) {
		mEventId = eventId;
	}

	public UUID getId() {
		return mId;
	}
	
}
