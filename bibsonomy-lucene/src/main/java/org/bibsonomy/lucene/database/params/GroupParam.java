package org.bibsonomy.lucene.database.params;

/**
 * @author fei
 * @version $Id$
 */
public class GroupParam {
	private Integer groupID;
	private String groupName;
	
	/**
	 * @return the groupID
	 */
	public Integer getGroupID() {
		return groupID;
	}
	
	/**
	 * @param groupID the groupID to set
	 */
	public void setGroupID(Integer groupID) {
		this.groupID = groupID;
	}
	
	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	
	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
