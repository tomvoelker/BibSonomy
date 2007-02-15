package org.bibsonomy.database.newImpl.general;

import java.util.Set;

import org.bibsonomy.model.Group;
/*
 * TODO: implements group specific methods of LogicInterface; method descriptions there
 */
public class GroupDBManager {

	public Set<Group> getGroups(String string, int start, int end) {
		return null;
	}
	
	public Group getGroupDetails(String authUserName, String groupName) {
		return null;
	}
	
	public void deleteGroup(String groupName) {

	}
	
	public void removeUserFromGroup(String groupName, String userName) {

	}
	
	public void addUserToGroup(String groupName, String userName) {

	}
	
	public void storeGroup(Group group, boolean update) {

	}
	
}
