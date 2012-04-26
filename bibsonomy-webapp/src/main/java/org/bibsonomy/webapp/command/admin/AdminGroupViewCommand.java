package org.bibsonomy.webapp.command.admin;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.model.Group;
import org.bibsonomy.webapp.command.BaseCommand;


/**
 * Command bean for admin page 
 * 
 * @author bsc
 * @version $Id$
 */
public class AdminGroupViewCommand extends BaseCommand {	
	
	/** specific action for admin page */
	private String action;
	
	/**
	 * Privacy options for the group
	 * FIXME: use generic handling to localize messages
	 * @see http://www.springjutsu.org/2011/03/binding-enums-with-i8n-localization-support/
	 */
	@Deprecated 
	private final Map<String, Privlevel> privlevel;
	
	private String adminResponse = "";
	private Group group = new Group();
	
	
	public AdminGroupViewCommand() {
		privlevel = new HashMap<String, Privlevel>();
		privlevel.put("Member list hidden", Privlevel.HIDDEN);
		privlevel.put("Member list public", Privlevel.PUBLIC);
		privlevel.put("Members can list members", Privlevel.MEMBERS);
	}

	/**
	 * @return the privlevels
	 */
	public Map<String, Privlevel> getPrivlevel() {
		return this.privlevel;
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
	public void setGroup(final Group group) {
		this.group = group;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(final String action) {
		this.action = action;
	}

	/**
	 * @param adminResponse
	 */
	public void setAdminResponse(final String adminResponse) {
		this.adminResponse = adminResponse;
	}

	/**
	 * @return the admin response
	 */
	public String getAdminResponse() {
		return adminResponse;
	}
}