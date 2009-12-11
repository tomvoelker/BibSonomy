package org.bibsonomy.webapp.command.admin;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.BaseCommand;


/**
 * Command bean for admin page 
 * 
 * @author Beate Krause
 * @version $Id$
 */
public class AdminCommand extends BaseCommand{

	/** Automatic actions (linked) for this page */
	private final Map<String,String> actionTitles;
	
	/** Privacy options for the group */
	private final Map<String, Privlevel> privlevel;
	
	/** Selected privacy level */
	private Privlevel selPrivlevel;

	/** information about a specific user */
	private String aclUserInfo; 
	
	/** specific action for admin page */
	private String action; 

	/** specific user to show */
	private User user;
	
	/** group name of group to be added to the system */
	private String requestedGroupName;
	
	/** specific user information */
	private String adminResponse;

	public AdminCommand(){
	
		// set actions 
		actionTitles = new HashMap<String, String>();
		actionTitles.put("spam", "Flag / unflag spammers");
		actionTitles.put("lucene", "Manage lucene");
		
		// set privacy options
		privlevel = new HashMap<String, Privlevel>();
		privlevel.put("Member list hidden", Privlevel.HIDDEN);
		privlevel.put("Member list public", Privlevel.PUBLIC);
		privlevel.put("Members can list members", Privlevel.MEMBERS);
		
	}

	public Map<String, String> getActionTitles() {
		return actionTitles;
	}
	
	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAclUserInfo() {
		return this.aclUserInfo;
	}

	public void setAclUserInfo(String aclUserInfo) {
		this.aclUserInfo = aclUserInfo;
	}
	
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getRequestedGroupName() {
		return this.requestedGroupName;
	}

	public void setRequestedGroupName(String requestedGroupName) {
		this.requestedGroupName = requestedGroupName;
	}
	
	
	public Map<String, Privlevel> getPrivlevel() {
		return this.privlevel;
	}
	
	public Privlevel getSelPrivlevel() {
		return this.selPrivlevel;
	}

	public void setSelPrivlevel(Privlevel selPrivlevel) {
		this.selPrivlevel = selPrivlevel;
	}
	
	public String getAdminResponse() {
		return this.adminResponse;
	}

	public void setAdminResponse(String adminResponse) {
		this.adminResponse = adminResponse;
	}

}