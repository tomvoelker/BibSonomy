package org.bibsonomy.webapp.command.actions;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.PostCommand;

/**
 * @author mwa
 * @version $Id$
 */
public class GroupAdminCommand extends PostCommand {

	private boolean userLoggedIn;
	
	private Group group;
	
	private String requestedGroup;
	
	private String setName;
	
	private List<Tag> tags;
	
	/**
	 * inits values
	 */
	public GroupAdminCommand(){
		group = new Group();
		tags = new ArrayList<Tag>();
	}
	
	/**
	 * @return the userLoggedIn
	 */
	public boolean isUserLoggedIn() {
		return this.userLoggedIn;
	}

	/**
	 * @param userLoggedIn the userLoggedIn to set
	 */
	public void setUserLoggedIn(boolean userLoggedIn) {
		this.userLoggedIn = userLoggedIn;
	}
	
	/**
	 * @return the group
	 */
	public Group getGroup() {
		return this.group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	/**
	 * @return the requestedGroup
	 */
	public String getRequestedGroup() {
		return this.requestedGroup;
	}

	/**
	 * @param requestedGroup the requestedGroup to set
	 */
	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

	/**
	 * @return the setName
	 */
	public String getSetName() {
		return this.setName;
	}

	/**
	 * @param setName the setName to set
	 */
	public void setSetName(String setName) {
		this.setName = setName;
	}

	/**
	 * @return the tags
	 */
	public List<Tag> getTags() {
		return this.tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

}
