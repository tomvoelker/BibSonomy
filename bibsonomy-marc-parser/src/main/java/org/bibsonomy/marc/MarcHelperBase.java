package org.bibsonomy.marc;

import java.util.ArrayList;
import java.util.List;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

/**
 * @author jensi
 */
public class MarcHelperBase {
	private static final String[] empty = new String[0];
	protected Record record;
	
	public MarcHelperBase(Record record)
	{
        this.record = record;
        
        // if raw record format is pica plus then process the pica record
    }
	
	protected String trim(String s) {
		if (s == null) {
			return null;
		}
		return s.trim();
	}
	
	public String[] _getFieldArray(String fieldNr, String[] fieldLetters) {
		return _getFieldArray(fieldNr, fieldLetters, true);
	}
	
	public String[] _getFieldArray(String fieldNr, String[] fieldLetters, boolean dummy) {
		ArrayList<String> rVal = new ArrayList<String>();
		List<DataField> tmp1 = (List<DataField>) record.getVariableFields("245");
		for (String c : fieldLetters) { 
			for (DataField df : tmp1) {
				Subfield sf = df.getSubfield(c.charAt(0));
				if (sf != null) {
					rVal.add(sf.getData());
				}
			}
		}
    	return rVal.toArray(empty);
	}
	
    protected String _getFirstFieldValue(String string, String[] fieldLetters) {
    	List<DataField> tmp1 = (List<DataField>) record.getVariableFields("245");
		for (String c : fieldLetters) { 
			for (DataField df : tmp1) {
				Subfield sf = df.getSubfield(c.charAt(0));
				if (sf != null) {
					return sf.getData();
				}
			}
		}
		return null;
	}
    
    protected int strlen(String s) {
    	if (s != null) {
    		return 0;
    	}
    	return s.length();
    }
    
    protected boolean dummyFalse() {
		return false;
	}
	
	protected String[] array(String...strings) {
		return strings;
	}
	
	protected int count(String[] arr) {
		return arr.length;
	}
	
	protected String[] array_merge(String[]...arrs) {
		int l = 0;
		for (String[] arr : arrs) {
			l += arr.length;
		}
		String[] rVal = new String[l];
		int i = 0;
		for (String[] arr : arrs) {
			for (int j = 0; j < arr.length; ++j) {
				rVal[i++] = arr[j];
			}
		}
		return rVal;
	}
	
	protected int strpos(String str, String query) {
		return str.indexOf(query);
	}
	
	protected String removeFirstChar(String str, String charToReplace) {
		if (str == null) {
			return null;
		}
		int i = str.indexOf(charToReplace);
		if (i < 0) {
			return str;
		}
		return str.substring(0, i) + str.substring(i + 1, str.length());
	}
}
