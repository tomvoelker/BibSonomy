/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
import java.util.List;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.AdminGroupOperation;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupLevelPermission;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.util.MailUtils;
import org.bibsonomy.webapp.command.admin.AdminGroupViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;

/**
 * Controller for group admin page
 *
 *
 * @author bsc, tni
 */
public class AdminGroupController implements MinimalisticController<AdminGroupViewCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(AdminGroupController.class);

	private LogicInterface logic;
	private MailUtils mailUtils;
	private Errors errors;

	@Override
	public View workOn(final AdminGroupViewCommand command) {
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		/*
		 * check user role
		 * If user is not logged in or not an admin: show error message
		 */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("please log in as admin");
		}

		/* Check for and perform the specified action */
		final AdminGroupOperation action = command.getAction();
		if (present(action)) {
			Group group = command.getGroup();
			User requestingUser;
			switch(action) {
			case ACCEPT_GROUP:
				group = this.logic.getGroupDetails(group.getName(), true);

				requestingUser = this.logic.getUserDetails(group.getGroupRequest().getUserName());
				this.logic.updateGroup(group, GroupUpdateOperation.ACTIVATE, null);
				if (present(requestingUser.getEmail())) {
					this.mailUtils.sendGroupActivationNotification(group, requestingUser, LocaleUtils.toLocale(requestingUser.getSettings().getDefaultLanguage()));
				}
				return new ExtendedRedirectView("/admin/group");
			case DECLINE_GROUP:
				final String groupName = group.getName();
				group = this.logic.getGroupDetails(groupName, true);

				requestingUser = this.logic.getUserDetails(group.getGroupRequest().getUserName());

				// delete the group
				log.debug("grouprequest for group \"" + groupName + "\" declined");
				this.logic.deleteGroup(groupName, true);

				// send mail
				String declineMessage = command.getDeclineMessage();
				if (!present(declineMessage)) {
					declineMessage = "";
				}
				if (present(requestingUser.getEmail())) {
					this.mailUtils.sendGroupDeclineNotification(groupName, declineMessage, requestingUser, LocaleUtils.toLocale(requestingUser.getSettings().getDefaultLanguage()));
				}
				return new ExtendedRedirectView("/admin/group");
			case FETCH_GROUP_SETTINGS:
				this.setGroupOrMarkNonExistent(command);
				break;
			case UPDATE_PERMISSIONS:
				this.updateGroupPermissions(command);
				break;
			case DELETE_GROUP:
				final Group groupToDelete = this.logic.getGroupDetails(command.getGroup().getName(), false);

				if (!present(groupToDelete)) {
					this.errors.reject("No such group.");
					command.setGroup(null);
					break;
				}

				if (present(groupToDelete.getPendingMemberships())) {
					for (final GroupMembership ms : groupToDelete.getPendingMemberships()) {
						this.logic.updateGroup(groupToDelete, GroupUpdateOperation.REMOVE_INVITED, ms);
					}
				}

				final List<GroupMembership> groupMembers = groupToDelete.getMemberships();

				for (final GroupMembership ms: groupMembers) {
					if (!ms.getUser().getName().equals(groupToDelete.getName())) {
						this.logic.updateGroup(groupToDelete, GroupUpdateOperation.REMOVE_MEMBER, ms);
					}
				}

				// TODO: log that the admin removed the user from the group
				this.logic.deleteGroup(groupToDelete.getName(), false);
				command.setGroup(null);
				break;
			default:
				break;
			}
		}

		// load the pending groups
		command.setPendingGroups(this.logic.getGroups(true, null, 0, Integer.MAX_VALUE));
		return Views.ADMIN_GROUP;
	}

	/**
	 * TODO: Documentation.
	 */
	private void updateGroupPermissions(final AdminGroupViewCommand command) {
		final Group dbGroup = this.getGroupOrMarkNonExistent(command);
		if (present(dbGroup) && GroupID.INVALID.getId() != dbGroup.getGroupId()) {
			dbGroup.setGroupLevelPermissions(new HashSet<GroupLevelPermission>());
			if (command.isCommunityPostInspectionPermission()) {
				dbGroup.addGroupLevelPermission(GroupLevelPermission.COMMUNITY_POST_INSPECTION);
				command.setCommunityPostInspectionPermission(false);
			}
			try {
				this.logic.updateGroup(dbGroup, GroupUpdateOperation.UPDATE_PERMISSIONS, null);
				command.setAdminResponse("settings.group.update.success");
				command.setPermissionsUpdated(true);
				command.setGroup(null);
			} catch (final IllegalArgumentException e) {
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
		final Group dbGroup = this.logic.getGroupDetails(command.getGroup().getName(), false);

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

	/**
	 * @param mailUtils the mailUtils to set
	 */
	public void setMailUtils(final MailUtils mailUtils) {
		this.mailUtils = mailUtils;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.bibsonomy.webapp.util.ErrorAware#getErrors()
	 */
	@Override
	public Errors getErrors() {
		return this.errors;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.bibsonomy.webapp.util.ErrorAware#setErrors(org.springframework.
	 * validation.Errors)
	 */
	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}
}