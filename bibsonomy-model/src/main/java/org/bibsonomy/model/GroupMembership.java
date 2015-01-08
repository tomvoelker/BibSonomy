package org.bibsonomy.model;

import org.bibsonomy.common.enums.GroupRole;

/**
 * Represents a membership of a user in a specific group and the role she
 * represents there.
 * 
 * @author niebler
 */
public class GroupMembership {
	
	private User user;
	private GroupRole groupRole;
	private boolean userSharedDocuments;
	
	/**
	 * default constructor
	 */
	public GroupMembership() {
		// noop
	}
	
	/**
	 * 
	 * @param user
	 * @param groupRole
	 * @param userSharedDocuments
	 */
	public GroupMembership(User user, GroupRole groupRole, boolean userSharedDocuments) {
		this.user = user;
		this.groupRole = groupRole;
		this.userSharedDocuments = userSharedDocuments;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the groupRole
	 */
	public GroupRole getGroupRole() {
		return this.groupRole;
	}

	/**
	 * @param groupRole the groupRole to set
	 */
	public void setGroupRole(GroupRole groupRole) {
		this.groupRole = groupRole;
	}

	/**
	 * @return the userSharedDocuments
	 */
	public boolean isUserSharedDocuments() {
		return this.userSharedDocuments;
	}

	/**
	 * @param userSharedDocuments the userSharedDocuments to set
	 */
	public void setUserSharedDocuments(boolean userSharedDocuments) {
		this.userSharedDocuments = userSharedDocuments;
	}
}
