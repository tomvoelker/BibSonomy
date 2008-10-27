package org.bibsonomy.webapp.command.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.TagSet;
import org.bibsonomy.webapp.command.PostCommand;

/**
 * @author mwa
 * @version $Id$
 */
public class GroupAdminCommand extends PostCommand{

	private boolean userLoggedIn;
	
	private Group group;
	
	private String requestedGroup;
	
	private String setName;
	
	private List<Tag> tags;
	
	public GroupAdminCommand(){
		group = new Group();
		tags = new ArrayList<Tag>();
	}
	
	public void setUserLoggedIn(boolean userLoggedIn) {
		this.userLoggedIn = userLoggedIn;
	}

	public boolean isUserLoggedIn() {
		return userLoggedIn;
	}
	
	public Group getGroup() {
		return this.group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
	
	public String getRequestedGroup() {
		return this.requestedGroup;
	}

	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

	public String getSetName() {
		return this.setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public List<Tag> getTags() {
		return this.tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

}
