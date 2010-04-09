package org.bibsonomy.database.params;

/**
 * @author mwa
 * @version $Id$
 */
public class TagSetParam {
	private String setName;
	private String tagName;
	private int groupId;
	
	/** Get the name of the tag set. 
	 * Each tag set has a name which uniquely identifies it among the groups tagsets.
	 * 
	 * @return The name of this tag set.
	 */
	public String getSetName() {
		return this.setName;
	}
	
	/** Set the name of the tag set.
	 * Each tag set has a name which uniquely identifies it among the groups tagsets.
	 * @param setName
	 */
	public void setSetName(String setName) {
		this.setName = setName;
	}
	
	/** Get the name of the tag associated with this tag set.
	 * @return The name of the tag.
	 */
	public String getTagName() {
		return this.tagName;
	}
	
	/** Set the name of the tag associated with this tag set. 
	 * @param tagName
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	/**
	 * @return The id of the group this tag set belongs to.
	 */
	public int getGroupId() {
		return this.groupId;
	}
	
	/**
	 * @param groupId
	 */
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
}
