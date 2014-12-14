package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.model.Group;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * Command for the /updateGroup page
 * @author niebler
 */
public class UpdateGroupCommand extends BaseCommand implements Serializable {
	
	// TODO: Find out why on earth spring needs this and remove it again!
	private String requestedGroup;
	
	private Group group;
	private String groupname;
	private GroupUpdateOperation operation;
	private int privlevel;
	private int sharedDocuments;
	private String username;
	
	private String realname;
	private String homepage;
	private String description;

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupName) {
		this.groupname = groupName;
	}

	public GroupUpdateOperation getOperation() {
		return operation;
	}

	public void setOperation(GroupUpdateOperation operation) {
		this.operation = operation;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRequestedGroup() {
		return requestedGroup;
	}

	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
