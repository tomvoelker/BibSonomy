package org.bibsonomy.model;

import org.bibsonomy.common.enums.GroupRole;

/**
 * Represents a membership of a user in a specific group and the role she
 * represents there.
 * @author niebler
 */
public class GroupMembership {
	
	private User user;
	private GroupRole groupRole;
	private boolean userSharedDocuments;
	
	public GroupMembership() {
		
	}
	
	public GroupMembership(User user, GroupRole groupRole, boolean userSharedDocuments) {
		this.user = user;
		this.groupRole = groupRole;
		this.userSharedDocuments = userSharedDocuments;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public GroupRole getGroupRole() {
		return groupRole;
	}

	public void setGroupRole(GroupRole groupRole) {
		this.groupRole = groupRole;
	}

	public boolean isUserSharedDocuments() {
		return userSharedDocuments;
	}

	public void setUserSharedDocuments(boolean userSharedDocuments) {
		this.userSharedDocuments = userSharedDocuments;
	}

	@Override
	public String toString() {
		return this.user + " " + this.groupRole + " " + this.userSharedDocuments;
	}
	
}
