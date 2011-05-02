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
	/** Privacy options for the group */
	private final Map<String, Privlevel> privlevel;
	
	private String adminResponse = "";
	private String requestedGroupName;

	private long selectedGroupID;
	private String selectedGroupName;
	private int selectedPrivacyLevel;
	private boolean selectedSharedDocuments;
	private Group group;
	
	
	public AdminGroupViewCommand() {
		// set privacy options
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
	 * @return the requestedGroupname
	 */
	public String getRequestedGroupName() {
		return this.requestedGroupName;
	}
	
	/**
	 * @param requestedGroupname the requestedGroupname to set
	 */
	public void setRequestedGroupName(String requestedGroupName) {
		this.requestedGroupName = requestedGroupName;
	}
	
	/**
	 * @return the selectedGroupID
	 */
	public long getSelectedGroupID() {
		return this.selectedGroupID;
	}

	/**
	 * @param selectedGroupID the selectedGroupID to set
	 */
	public void setSelectedGroupID(long selectedGroupID) {
		this.selectedGroupID = selectedGroupID;
	}

	/**
	 * @return the selectedGroupName
	 */
	public String getSelectedGroupName() {
		return this.selectedGroupName;
	}

	/**
	 * @param selectedGroupName the selectedGroupName to set
	 */
	public void setSelectedGroupName(String selectedGroupName) {
		this.selectedGroupName = selectedGroupName;
	}

	/**
	 * @return the selectedPrivacyLevel as an enum-type
	 */
	public Privlevel getSelectedPrivlevel() {
		return Privlevel.getPrivlevel(this.selectedPrivacyLevel);
	}
	
	/**
	 * @return the selectedPrivacyLevel as an integer
	 */
	public int getSelectedPrivacyLevel() {
		return this.selectedPrivacyLevel;
	}

	/**
	 * @param selectedPrivacyLevel the selectedPrivacyLevel to set
	 */
	public void setSelectedPrivacyLevel(int selectedPrivacyLevel) {
		this.selectedPrivacyLevel = selectedPrivacyLevel;
	}

	/**
	 * @return the selectedSharedDocuments
	 */
	public boolean getSelectedSharedDocuments() {
		return this.selectedSharedDocuments;
	}

	/**
	 * @param selectedSharedDocuments the selectedSharedDocuments to set
	 */
	public void setSelectedSharedDocuments(boolean selectedSharedDocuments) {
		this.selectedSharedDocuments = selectedSharedDocuments;
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
	 * @return the action
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @param adminResponse
	 */
	public void setAdminResponse(String adminResponse) {
		this.adminResponse = adminResponse;
	}

	/**
	 * @return the admin response
	 */
	public String getAdminResponse() {
		return adminResponse;
	}
}