package org.bibsonomy.database.params;

/**
 * @author mwa
 * @version $Id$
 */
public class TagSetParam {
	String setName;
	String tagName;
	int groupId;
	
	public String getSetName() {
		return this.setName;
	}
	public void setSetName(String setName) {
		this.setName = setName;
	}
	public String getTagName() {
		return this.tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public int getGroupId() {
		return this.groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
}
