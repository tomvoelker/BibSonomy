package org.bibsonomy.webapp.controller.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.admin.AdminGroupViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for group admin page
 * 
 * @author bsc
 * @version $Id$
 */
public class AdminGroupController implements MinimalisticController<AdminGroupViewCommand> {
	private static final Log log = LogFactory.getLog(AdminGroupController.class);
	private LogicInterface logic;
	
	/* Possible actions */
	private static final String FETCH_GROUP_SETTINGS  = "fetchGroupSettings"; 
	private static final String UPDATE_GROUP 		  = "updateGroup";  
	private static final String CREATE_GROUP          = "createGroup";

	
	@Override
	public View workOn(AdminGroupViewCommand command) {
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		
		/* Check user role
		 * If user is not logged in or not an admin: show error message */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("error.method_not_allowed");
		}
		
		/* Check for and perform the specified action */
		if(command.getAction() == null || command.getAction().isEmpty()) {
			log.debug("No action specified.");
		}
		else if (command.getAction().equals(FETCH_GROUP_SETTINGS)) {
			fetchGroupSettings(command);
		}
		else if (command.getAction().equals(UPDATE_GROUP)) {
			updateGroup(command);
		}
		else if (command.getAction().equals(CREATE_GROUP)) {
			createGroup(command);
		}
		
		command.setPageTitle("admin group");
		return Views.ADMIN_GROUP;
	}

	
	/** Create a new group. */
	private void createGroup(AdminGroupViewCommand command) {
		// Check if group already exists
		Group fetchedGroup = logic.getGroupDetails(command.getSelectedGroupName());
		if (fetchedGroup != null) {
			command.setAdminResponse("Group already exists!");
			
		// Create group
		} else {
			Group newGroup = applyGroupSettings(new Group(), command);
			logic.createGroup(newGroup);
			command.setAdminResponse("Successfully created new group!");
		}
	}

	/** Update the settings of a group. */
	private void updateGroup(AdminGroupViewCommand command) {
		fetchGroupSettings(command);
		if (command.getGroup() != null) {
			Group updatedGroup = applyGroupSettings(command.getGroup(), command);
			logic.updateGroup(updatedGroup, GroupUpdateOperation.UPDATE_SETTINGS);
			command.setAdminResponse("Group updated successfully!");
		} else {
			command.setAdminResponse("Update group failed: a group with that name does not exist!");
		}
	}

	/** Fetch and show the current settings of a group. */
	private void fetchGroupSettings(AdminGroupViewCommand command) {
		// TODO: remove logmessage or change loglevel 
		log.info("Fetching details for group \"" + command.getSelectedGroupName() + "\"");
		Group fetchedGroup = logic.getGroupDetails(command.getSelectedGroupName());
		
		if (fetchedGroup != null) {
		    command.setGroup(fetchedGroup);
		} else {
			command.setAdminResponse("The group \"" + command.getRequestedGroupName() + "\" does not exist.");
		}
	}

	/**
	 * Change the settings of a group to those specified in the command.
	 * @param group the group which is going to be updated
	 * @param command the command which contains the new settings
	 */
	private Group applyGroupSettings(Group group, AdminGroupViewCommand command) {
		group.setName(command.getSelectedGroupName());
		group.setPrivlevel(Privlevel.getPrivlevel(command.getSelectedPrivacyLevel()));
		group.setSharedDocuments(command.getSelectedSharedDocuments());
		
		return group;
	}

	@Override
	public AdminGroupViewCommand instantiateCommand() {
		return new AdminGroupViewCommand();
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}