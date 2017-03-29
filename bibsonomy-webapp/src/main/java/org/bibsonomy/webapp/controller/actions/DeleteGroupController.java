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
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.webapp.command.GroupSettingsPageCommand;
import org.bibsonomy.webapp.controller.GroupSettingsPageController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.DeleteGroupValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;
import org.bibsonomy.util.ExceptionUtils;

/**
 * @author Mario Holtmüller
 */
public class DeleteGroupController extends GroupSettingsPageController implements ValidationAwareController<GroupSettingsPageCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(UpdateGroupController.class);	
	
	/** hold current errors */
	private Errors errors = null;
	
	@Override
	public View workOn(final GroupSettingsPageCommand command) {
		final RequestWrapperContext context = command.getContext();
		
		/*
		 * user has to be logged in to delete himself
		 */
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}
		
		// TODO: why we have to set the loggedin user? TODO_GROUPS
		command.setUser(context.getLoginUser());
		
		final User loginUser = context.getLoginUser();
		command.setLoggedinUser(loginUser);
		
		// group exists?
		final Group group = this.logic.getGroupDetails(command.getGroupname(), false);
		if (!present(group)) {
			throw new IllegalStateException("The requested group does not exist.");
		}
		command.setGroup(group);
		command.setRequestedGroup(group.getName());
		
		// check if the logged in user is a member of this group (and no pending user)
		final GroupMembership groupMembership = GroupUtils.getGroupMembershipForUser(group, loginUser.getName(), false);
		if (!present(groupMembership)) {
			throw new AccessDeniedException("You are not allowed to view this page");
		}
		
		command.setGroupMembership(groupMembership);
		
		// the last user must be an administrator of the group 
		final GroupRole roleOfLoggedinUser = groupMembership.getGroupRole();
		if (!GroupRole.ADMINISTRATOR.equals(roleOfLoggedinUser)) {
			throw new AccessDeniedException("You are not allowed to view this page");
		}
		
		// ensure that the group has no members except the admin. size > 2 because the group user is also part of the membership list or > 1 to cover old groups
		if (group.getMemberships().size() > 2 || (group.getMemberships().size() > 1 && (group.getMemberships().get(0).getGroupRole() != GroupRole.DUMMY && group.getMemberships().get(1).getGroupRole() != GroupRole.DUMMY))) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + group.getName() + "') has at least one member beside the administrator.");
		}
		
		/*
		 * check the ckey
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
		}
		/*
		 * go back to the group settings page and display errors from command field
		 * validation
		 */
		if (errors.hasErrors()) {
			return super.workOn(command);
		}
		
		final String groupName = group.getName();
		log.debug("User is logged in, ckey is valid, deleting group " + groupName);
		this.logic.deleteGroup(groupName, false, false);
		
		command.setMessage("success.groupDelete", Collections.singletonList(groupName));
		return Views.SUCCESS;
	}

	@Override
	public Validator<GroupSettingsPageCommand> getValidator() {
		return new DeleteGroupValidator();
	}

	@Override
	public boolean isValidationRequired(final GroupSettingsPageCommand command) {
		return true;
	}

	
	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

}
