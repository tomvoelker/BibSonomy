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

import java.util.HashSet;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.AdminGroupOperation;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupLevelPermission;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.util.MailUtils;
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
// TODO: Make ErrorAware for proper error messages
public class AdminGroupController implements MinimalisticController<AdminGroupViewCommand> {
	private static final Log log = LogFactory.getLog(AdminGroupController.class);
	private LogicInterface logic;
	private MailUtils mailUtils;

	@Override
	public View workOn(final AdminGroupViewCommand command) {
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();
		command.setPermissionsUpdated(false);
		
		/* Check user role
		 * If user is not logged in or not an admin: show error message */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("please log in as admin");
		}

		/* Check for and perform the specified action */
		final AdminGroupOperation action = command.getAction();
		User requestingUser;
		if(!present(action)) {
			log.debug("No action specified.");
		} else {
			Group group = command.getGroup();
			switch(action) {
				case ACCEPT:
					for (Group g : logic.getGroups(true, 0, Integer.MAX_VALUE)) {
						if (g.getName().equals(group.getName())) {
							group = g;
							break;
						}
					}
					
					requestingUser = this.logic.getUserDetails(group.getGroupRequest().getUserName());
					this.logic.updateGroup(group, GroupUpdateOperation.ACTIVATE, null);
					if (present(requestingUser.getEmail())) {
						this.mailUtils.sendGroupActivationNotification(group, requestingUser, LocaleUtils.toLocale(requestingUser.getSettings().getDefaultLanguage()));
					}
					break;
				case CREATE:
					command.setAdminResponse(createGroup(group));
					requestingUser = this.logic.getUserDetails(group.getGroupRequest().getUserName());
					if (present(requestingUser.getEmail())) {
						this.mailUtils.sendGroupActivationNotification(group, requestingUser, LocaleUtils.toLocale(requestingUser.getSettings().getDefaultLanguage()));
					}
					break;
				case DECLINE:
					log.debug("grouprequest for group \"" + group.getName() + "\" declined");
					this.logic.updateGroup(group, GroupUpdateOperation.DELETE, null);
					// TODO: send mail
					break;
				case FETCH_GROUP_SETTINGS:
					setGroupOrMarkNonExistent(command);
					break;
				case UPDATE:
					updateGroup(command);
					break;
				case UPDATE_PERMISSIONS:
					this.updateGroupPermissions(command);
					break;
				default:
					break;
			}
				
		}
		
		// load the pending groups
		command.setPendingGroups(logic.getGroups(true, 0, Integer.MAX_VALUE));
	
		return Views.ADMIN_GROUP;
	}

	/**
	 * Create a new group
	 * TODO: Proper Error messages.
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
		 * check database for existing group or pending group
		 */
		if (logic.getGroups(false, 0, Integer.MAX_VALUE).contains(group)
				|| logic.getGroups(true, 0, Integer.MAX_VALUE).contains(group)) {
			return "Group already exists!";
		}
		
		// Create the group ...
		logic.createGroup(group);
		// ... and activate it
		logic.updateGroup(group, GroupUpdateOperation.ACTIVATE, null);
		return "Successfully created new group " + group.getName() + "!";
	}

	/**
	 * Update the settings of a group.
	 * 
	 * TODO: Find out when this is used.
	 * 
	 */
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

	
	/**
	 * TODO: Documentation.
	 */
	private void updateGroupPermissions(final AdminGroupViewCommand command) {
		final Group dbGroup = getGroupOrMarkNonExistent(command);
		if (present(dbGroup) && GroupID.INVALID.getId() != dbGroup.getGroupId()) {
			dbGroup.setGroupLevelPermissions(new HashSet<GroupLevelPermission>());
			if (command.isCommunityPostInspectionPermission()) {
				dbGroup.addGroupLevelPermission(GroupLevelPermission.COMMUNITY_POST_INSPECTION);
				command.setCommunityPostInspectionPermission(false);
			}
			try {
				logic.updateGroup(dbGroup, GroupUpdateOperation.UPDATE_PERMISSIONS, null);
				command.setAdminResponse("settings.group.update.success");
				command.setPermissionsUpdated(true);
				command.setGroup(null);
			} catch (IllegalArgumentException e) {
				command.setAdminResponse(e.getMessage());
			}
		}
	}

	/**
	 * TODO: Documentation.
	 */
	private void setGroupOrMarkNonExistent(final AdminGroupViewCommand command) {
		final Group dbGroup = this.getGroupOrMarkNonExistent(command);
		if (present(dbGroup)) {
			command.setGroup(dbGroup);
		}
	}
	
	/**
	 * TODO: Documentation.
	 */
	private Group getGroupOrMarkNonExistent(final AdminGroupViewCommand command) {
		final Group dbGroup = logic.getGroupDetails(command.getGroup().getName());

		if (!GroupUtils.isValidGroup(dbGroup)) {
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

	public void setMailUtils(MailUtils mailUtils) {
		this.mailUtils = mailUtils;
	}
}