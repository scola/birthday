package io.github.scola.birthday;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Birthday {
	
    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_DATE = "date";
    private static final String JSON_TIME = "time";
    private static final String JSON_LUNAR = "lunar";
    private static final String JSON_EARLY = "early";
    private static final String JSON_REPEAT = "repeat";
    private static final String JSON_METHOD = "method";    
	
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
        mDate = "06-08";
        mTime = "12:00";
        isLunar = true;
        isEarly = false;
        mRepeat = 10;
        mMethod = "Email";
        mEventId = new ArrayList<String>();
    }
    
    public Birthday(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        mName = json.getString(JSON_NAME);
        mDate = json.getString(JSON_DATE);
        mTime = json.getString(JSON_TIME);
        isLunar = json.getBoolean(JSON_LUNAR);
        isEarly = json.getBoolean(JSON_EARLY);
        mRepeat = json.getInt(JSON_REPEAT);
        mMethod = json.getString(JSON_METHOD);     
        //event id not available
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_NAME, mName);
        json.put(JSON_DATE, mDate);
        json.put(JSON_TIME, mTime);
        json.put(JSON_LUNAR, isLunar);
        json.put(JSON_EARLY, isEarly);
        json.put(JSON_REPEAT, mRepeat);
        json.put(JSON_METHOD, mMethod);
        //event id not available
        return json;
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
