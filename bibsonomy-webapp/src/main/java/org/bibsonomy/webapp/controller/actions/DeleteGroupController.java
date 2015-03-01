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
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.webapp.command.GroupSettingsPageCommand;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.controller.GroupSettingsPageController;
import org.bibsonomy.webapp.controller.SettingsPageController;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.DeleteGroupValidator;
import org.bibsonomy.webapp.validation.DeleteUserValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;

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
		
		command.setUser(context.getLoginUser());
						
		final User loginUser = command.getContext().getLoginUser();
		command.setLoggedinUser(loginUser);
		
		// group exists?
		final Group group = this.logic.getGroupDetails(command.getGroupname());
		if (!present(group)) {
			throw new AccessDeniedException("The requested group does not exist.");
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
		if (roleOfLoggedinUser != GroupRole.ADMINISTRATOR) {
			throw new AccessDeniedException("You are not allowed to view this page");
		}
				
		// size must be bigger than 2 because the membership object contains also the group user
		if(group.getMemberships().size() > 2) {
			throw new AccessDeniedException("TODO: add error, group not empty!!");
		}
		
		/*
		 * go back to the group settings page and display errors from command field
		 * validation
		 */
		if (errors.hasErrors()) {
			return Views.GROUPSETTINGSPAGE;
		}
		
		/*
		 * check the ckey
		 */
		if (context.isValidCkey()){
			log.debug("User is logged in, ckey is valid ... check the security answer");
			
			/*
			 * check the security input …
			 */
			if ("yes".equalsIgnoreCase(command.getDelete())) {
				/*
				 * all fine -> delete the group
				 */
				
				final String groupName = group.getName();
				log.debug("answer is correct - deleting group: " + groupName);
				try {
					logic.deleteGroup(groupName);
				} catch (final UnsupportedOperationException ex) {
					// needed?
				}
			} else {
				/*
				 * … else add an error
				 */
				errors.reject("error.secure.answer");
			}
		} else {
			errors.reject("error.field.valid.ckey");
		}
		

		if (errors.hasErrors()){
			return super.workOn(command);
		}
		
		//return new ExtendedRedirectView("/");
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
