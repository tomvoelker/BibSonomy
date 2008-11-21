package org.bibsonomy.webapp.command;

import org.bibsonomy.model.Group;


/**
 * Bean for Group-Sites
 *
 * @author  Stefan Stuetzer
 * @version $Id$
 */
public class GroupResourceViewCommand extends TagResourceViewCommand {

	/** the group whose resources are requested*/
	private String requestedGroup = "";
	
	/** bean for group members */
	private Group group;
	
	/**
	 * @return requestedGroup name of the group whose resources are requested
	 */
	public String getRequestedGroup() {
		return this.requestedGroup;
	}

	/**
	 *  @param requestedGroup name of the group whose resources are requested
	 */
	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

	/** Get the group associated with this command.
	 * 
	 * @return The group associated with this command.
	 */
	public Group getGroup() {
		return this.group;
	}

	/** Set the group associated with this command
	 * @param group
	 */
	public void setGroup(Group group) {
		this.group = group;
	}		
}