package org.bibsonomy.webapp.command;


/**
 * Bean for Group-Sites
 *
 * @author  Stefan Stuetzer
 * @version $Id$
 */
public class GroupResourceViewCommand extends TagResourceViewCommand {

	/** the group whode resources are requested*/
	private String requestedGroup = "";
		
	/** bean for group members */
	private GroupMemberCommand memberCommand = new GroupMemberCommand();
	
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
	
	/**
	 *  @return command with all group members (in dependence of the group privacy level)
	 */
	public GroupMemberCommand getMemberCommand() {
		return this.memberCommand;
	}

	/**
	 * @param memberCommand command with group members
	 */
	public void setMemberCommand(GroupMemberCommand memberCommand) {
		this.memberCommand = memberCommand;
	}	
}