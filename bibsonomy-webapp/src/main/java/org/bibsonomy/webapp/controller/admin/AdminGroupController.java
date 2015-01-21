/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.admin;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.admin.AdminGroupViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

/**
 * Controller for group admin page
 * 
 * @author bsc
 */
// TODO: Needs loads of polishing
public class AdminGroupController implements MinimalisticController<AdminGroupViewCommand> {
	private static final Log log = LogFactory.getLog(AdminGroupController.class);
	private LogicInterface logic;

	/* Possible actions */
	private static final String FETCH_GROUP_SETTINGS  = "fetchGroupSettings"; 
	private static final String UPDATE_GROUP 		  = "updateGroup";  
	private static final String CREATE_GROUP          = "createGroup";  
	private static final String ACCEPT_GROUP          = "acceptGroup"; 
	private static final String DECLINE_GROUP          = "declineGroup";


	@Override
	public View workOn(final AdminGroupViewCommand command) {
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		/* Check user role
		 * If user is not logged in or not an admin: show error message */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("please log in as admin");
		}

		/* Check for and perform the specified action */
		final String action = command.getAction();
		if(!present(action)) {
			log.debug("No action specified.");
		
		} else if (FETCH_GROUP_SETTINGS.equals(action)) {
			final Group dbGroup = this.getGroupOrMarkNonExistent(command);
			if (present(dbGroup)) {
				command.setGroup(dbGroup);
			}

		} else if (UPDATE_GROUP.equals(action)) {
			updateGroup(command);
		
		} else if (CREATE_GROUP.equals(action)) {
			command.setAdminResponse(createGroup(command.getGroup()));
		
		} else if (ACCEPT_GROUP.equals(action)) {
			final String groupName = command.getGroup().getName();
			log.debug("accepting group \""+groupName+"\"");
			this.logic.updateGroup(command.getGroup(), GroupUpdateOperation.ACTIVATE, null);
		
		} else if (DECLINE_GROUP.equals(action)) {
			log.debug("grouprequest for group \""+command.getGroup().getName()+"\" declined");
			this.logic.updateGroup(command.getGroup(), GroupUpdateOperation.DELETE, null);
		
		} else if ("changePermissions".equals(action)) {
			this.updateGroupPermissions(command);
		} 
		// if the action is other than the accepted ones, we ignore it and just show the page again
		
		// load the pending groups
		command.setPendingGroups(logic.getGroups(true, 0, Integer.MAX_VALUE));
	
		return Views.ADMIN_GROUP;
	}



	/**
	 * Create a new group
	 * 
	 * @param command
	 */
	private String createGroup(final Group group) {
		/*
		 * Check if group-name is empty
		 */
		final String groupName = group.getName();
		if (!present(groupName)) {
			return "Group-creation failed: Group-name is empty!";
		}
		/*
		 * check database for existing group
		 */
		if (present(logic.getGroupDetails(groupName))) {
			return "Group already exists!";
		}
		
		// Create the group ...
		logic.createGroup(group);
		// ... and activate it
		logic.updateGroup(group, GroupUpdateOperation.ACTIVATE, null);
		return "Successfully created new group!";
	}

	/** Update the settings of a group. */
	private void updateGroup(final AdminGroupViewCommand command) {
		final Group dbGroup = getGroupOrMarkNonExistent(command);
		if (present(dbGroup)) {
			Group commandGroup = command.getGroup();
			dbGroup.setPrivlevel(commandGroup.getPrivlevel());
			dbGroup.setSharedDocuments(commandGroup.isSharedDocuments());

			logic.updateGroup(dbGroup, GroupUpdateOperation.UPDATE_SETTINGS, null);
			command.setAdminResponse("Group updated successfully!");
		}
	}

	
	/** Update the settings of a group. */
	private void updateGroupPermissions(final AdminGroupViewCommand command) {
		final Group dbGroup = getGroupOrMarkNonExistent(command);
		if (present(dbGroup)) {
			dbGroup.setGroupLevelPermissions(command.getGroup().getGroupLevelPermissions());

			logic.updateGroup(dbGroup, GroupUpdateOperation.UPDATE_PERMISSIONS, null);
			command.setAdminResponse("Group updated successfully!");
		}
	}

	private Group getGroupOrMarkNonExistent(final AdminGroupViewCommand command) {
		final Group dbGroup = logic.getGroupDetails(command.getGroup().getName());

		if (!present(dbGroup)) {
			command.setAdminResponse("The group \"" + command.getGroup().getName() + "\" does not exist.");
		}
		return dbGroup;
	}

	@Override
	public AdminGroupViewCommand instantiateCommand() {
		return new AdminGroupViewCommand();
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
}