package io.github.scola.birthday.preferences;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.widget.ListView;

import io.github.scola.birthday.R;

/**
 * 
 * @author declanshanaghy
 * http://blog.350nice.com/wp/archives/240
 * MultiChoice Preference Widget for Android
 *
 * @contributor matiboy
 * Added support for check all/none and custom separator defined in XML.
 * IMPORTANT: The following attributes MUST be defined (probably inside attr.xml) for the code to even compile
 * <declare-styleable name="ListPreferenceMultiSelect">
    	<attr format="string" name="checkAll" />
    	<attr format="string" name="separator" />
    </declare-styleable>
 *  Whether you decide to then use those attributes is up to you.
 *
 */
public class ListPreferenceMultiSelect extends ListPreference {
	private String separator;
	private static final String DEFAULT_SEPARATOR = "OV=I=XseparatorX=I=VO"; 
	private static final String LOG_TAG = "ListPreferenceMultiSelect";
	private String checkAllKey = null;
	private boolean[] mClickedDialogEntryIndices;
	private CharSequence mDefaultSummary;
	
	// Constructor
	public ListPreferenceMultiSelect(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ListPreferenceMultiSelect);
        checkAllKey = a.getString( R.styleable.ListPreferenceMultiSelect_checkAll );
        String s = a.getString(R.styleable.ListPreferenceMultiSelect_separator );
        if( s != null ) {
        	separator = s;
        } else {
        	separator = DEFAULT_SEPARATOR;
        }
     // Initialize the array of boolean to the same size as number of entries
        mClickedDialogEntryIndices = new boolean[getEntries().length];
        //mClickedDialogEntryIndices[0] = true;
        mDefaultSummary = getSummary();
    }
	
	@Override
    public void setEntries(CharSequence[] entries) {
    	super.setEntries(entries);
    	// Initialize the array of boolean to the same size as number of entries
        mClickedDialogEntryIndices = new boolean[entries.length];
    }
    
    public ListPreferenceMultiSelect(Context context) {
        this(context, null);
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
    	CharSequence[] entries = getEntries();
    	CharSequence[] entryValues = getEntryValues();
        if (entries == null || entryValues == null || entries.length != entryValues.length ) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array which are both the same length");
        }

        restoreCheckedEntries();
        builder.setMultiChoiceItems(entries, mClickedDialogEntryIndices, 
                new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which, boolean val) {
						if( isCheckAllValue( which ) == true ) {
							checkAll( dialog, val );
						}
						mClickedDialogEntryIndices[which] = val;
					}
        });
    }
    
    private boolean isCheckAllValue( int which ){
    	final CharSequence[] entryValues = getEntryValues();
    	if(checkAllKey != null) {
			return entryValues[which].equals(checkAllKey);
		}
    	return false;
    }
    
    private void checkAll( DialogInterface dialog, boolean val ) {
    	ListView lv = ((AlertDialog) dialog).getListView();
		int size = lv.getCount();
		for(int i = 0; i < size; i++) {
	        lv.setItemChecked(i, val);
	        mClickedDialogEntryIndices[i] = val;
	    }
    }

    public String[] parseStoredValue(CharSequence val) {
		if ( "".equals(val) ) {
			return null;
		}
		else {
			return ((String)val).split(separator);
		}
    }
    
    private void restoreCheckedEntries() {
    	CharSequence[] entryValues = getEntryValues();
    	String summary = getSummary().toString();
    	for ( int i=0; i<entryValues.length; i++ ) {
    		if(summary.contains(entryValues[i]))
    			mClickedDialogEntryIndices[i] = true;
    	}
    	/*    	
    	// Explode the string read in sharedpreferences
    	String[] vals = parseStoredValue(getValue());
    	
    	if ( vals != null ) {
    		List<String> valuesList = Arrays.asList(vals);
//        	for ( int j=0; j<vals.length; j++ ) {
//    		TODO: Check why the trimming... Can there be some random spaces added somehow? What if we want a value with trailing spaces, is that an issue?
//        		String val = vals[j].trim();
        	for ( int i=0; i<entryValues.length; i++ ) {
        		CharSequence entry = entryValues[i];
            	if ( valuesList.contains(entry) ) {
        			mClickedDialogEntryIndices[i] = true;
        		}
        	}
//        	}
    	} else {
    		mClickedDialogEntryIndices[0] = true;
    	}
    	*/
    }

	@Override
    protected void onDialogClosed(boolean positiveResult) {
//        super.onDialogClosed(positiveResult);
		ArrayList<String> values = new ArrayList<String>();
        
    	CharSequence[] entryValues = getEntryValues();
        if (positiveResult && entryValues != null) {
        	for ( int i=0; i<entryValues.length; i++ ) {
        		if ( mClickedDialogEntryIndices[i] == true ) {
        			// Don't save the state of check all option - if any
        			String val = (String) entryValues[i];
        			if( checkAllKey == null || (val.equals(checkAllKey) == false) ) {
        				values.add(val);
        			}
        		}
        	}

            if (callChangeListener(values)) {
        		setValue(join(values, separator));
            }
            
            if(values.isEmpty()) {
            	setSummary(mDefaultSummary);
            } else {
            	String summary = values.toString();
            	setSummary(summary.substring(1, summary.length() - 1));
            }
        }
    }
	
	// Credits to kurellajunior on this post http://snippets.dzone.com/posts/show/91
	protected static String join( Iterable< ? extends Object > pColl, String separator )
    {
        Iterator< ? extends Object > oIter;
        if ( pColl == null || ( !( oIter = pColl.iterator() ).hasNext() ) )
            return "";
        StringBuilder oBuilder = new StringBuilder( String.valueOf( oIter.next() ) );
        while ( oIter.hasNext() )
            oBuilder.append( separator ).append( oIter.next() );
        return oBuilder.toString();
    }
	
	// TODO: Would like to keep this static but separator then needs to be put in by hand or use default separator "OV=I=XseparatorX=I=VO"...
	/**
	 * 
	 * @param straw String to be found
	 * @param haystack Raw string that can be read direct from preferences
	 * @param separator Separator string. If null, static default separator will be used
	 * @return boolean True if the straw was found in the haystack
	 */
	public static boolean contains( String straw, String haystack, String separator ){
		if( separator == null ) {
			separator = DEFAULT_SEPARATOR;
		}
		String[] vals = haystack.split(separator);
		for( int i=0; i<vals.length; i++){
			if(vals[i].equals(straw)){
				return true;
			}
		}
		return false;
	}
}
