package helpers.picatobibtex;

import java.util.HashMap;
import java.util.Map;

/**
 * @author C. Kramer
 * @version $Id$
 */
public class Row {
	private String cat = null;
	private Map<String, SubField> subfields = new HashMap<String, SubField>();
	
	/**
	 * 
	 */
	public Row(){
		
	}
	
	/**
	 * @param cat
	 */
	public Row(final String cat){
		this.cat = cat;
	}

	/**
	 * Adds a subfield to the row object
	 * 
	 * @param sub
	 */
	public void addSubField(final SubField sub){
		this.subfields.put(sub.getSubTag(), sub);
	}

	/**
	 * Returns the category of the row
	 * 
	 * @return String
	 */
	public String getCat() {
		return this.cat;
	}

	/**
	 * Tests if the given subfield is existing in this row
	 * 
	 * @param sub
	 * @return boolean
	 */
	public boolean isExisting(final String sub){
		return subfields.containsKey(sub);
	}
	
	/**
	 * Returns the requested SubField
	 * 
	 * @param sub
	 * @return SubField
	 */
	public SubField getSubField(final String sub){
		return subfields.get(sub);
	}
	
	/**
	 * Return the complete SubField map of this row
	 * 
	 * @return Map<String, SubField>
	 */
	public Map<String, SubField> getSubFields(){
		return this.subfields;
	}
}
