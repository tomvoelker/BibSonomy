package org.bibsonomy.database.params;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;


public class UserParam {

private	String requestedUserName;
private	String requestedGroupName;
private	String userName;
private	String groupingName;
private	String regex;
private	int offset;
private	int limit;
private	int groupId;
private ConstantID groupType;
	
	
	private List<Integer> groups;
	 
	public int getLimit() {
		return this.limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getOffset() {
		return this.offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public String getGroupingName() {
		return this.groupingName;
	}
	public void setGroupingName(String groupingName) {
		this.groupingName = groupingName;
	}
	public String getRegex() {
		return this.regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRequestedUserName() {
		return this.requestedUserName;
	}
	public void setRequestedUserName(String requestedUsername) {
		this.requestedUserName = requestedUsername;
	}
	public List<Integer> getGroups() {
		return this.groups;
	}
	public void setGroups(List<Integer> groups) {
		this.groups = groups;
	}
	public String getRequestedGroupName() {
		return this.requestedGroupName;
	}
	public void setRequestedGroupName(String requestedGroupName) {
		this.requestedGroupName = requestedGroupName;
	}
	public int getGroupId() {
		return this.groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getGroupType() {
		return groupType.getId();
	}

	public void setGroupType(ConstantID groupType) {
		this.groupType = groupType;
	}
	
	
}
