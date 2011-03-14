package org.bibsonomy.model.extra;

import java.util.ArrayList;

/**
 * @author philipp
 * @version $Id$
 */
public class ExtendedFieldList {
    
    private String key;
    
    private ArrayList<String> valueList = new ArrayList<String>();

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * @param valueList 
	 */
	public void setValueList(ArrayList<String> valueList) {
		this.valueList = valueList;
	}

	/**
	 * @return the valueList
	 */
	public ArrayList<String> getValueList() {
		return valueList;
	}
    
    
    
}
