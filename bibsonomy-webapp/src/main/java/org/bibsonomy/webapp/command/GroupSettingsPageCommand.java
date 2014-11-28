package org.bibsonomy.webapp.command;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;

/**
 *
 * @author niebler
 */
public class GroupSettingsPageCommand extends BaseCommand {
	
	private Group group;
	private User loggedinUser;
	private String requestedGroup;
	private GroupMembership groupMembership;
	
	private String username;
	private int privlevel;
	private int sharedDocuments;

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public User getLoggedinUser() {
		return loggedinUser;
	}

	public void setLoggedinUser(User loggedinUser) {
		this.loggedinUser = loggedinUser;
	}

	public String getRequestedGroup() {
		return requestedGroup;
	}

	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getPrivlevel() {
		return privlevel;
	}

	public void setPrivlevel(int privlevel) {
		this.privlevel = privlevel;
	}

	public int getSharedDocuments() {
		return sharedDocuments;
	}

	public void setSharedDocuments(int sharedDocuments) {
		this.sharedDocuments = sharedDocuments;
	}
	
	public GroupMembership getGroupMembership() {
		return groupMembership;
	}

	public void setGroupMembership(GroupMembership groupMembership) {
		this.groupMembership = groupMembership;
	}
}
