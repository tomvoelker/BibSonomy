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
package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.GroupRequestCommand;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.validation.GroupValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;


/**
 * @author Mario Holtmueller
 */
public class GroupRequestController implements ValidationAwareController<GroupRequestCommand>, ErrorAware {

	
	private Errors errors = null;
	private LogicInterface logic;
	
	/**
	 * @param command
	 * @return the view
	 */
	@Override
	public View workOn(final GroupRequestCommand command) {
		final RequestWrapperContext context = command.getContext();

		/*
		 * user has to be logged in to see this page
		 */
		if (!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedNoticeException("please log in", "error.general.login");
		}
		
		final String action = command.getOperation();
		
		if (present(action)) {
			if (new String("REQUEST").equals(action)) {

				/*
				 * check the ckey
				 */
				if (!context.isValidCkey()) {
					errors.reject("error.field.valid.ckey");
				}
				
				final User loginUser = context.getLoginUser();
				final Group requestedGroup = command.getGroup();
				
				if (present(requestedGroup)) {
					
					if (!present(requestedGroup.getName())) {
						// TODO: add form error for field (rejectValue)
						this.errors.reject("settings.group.error.requestGroupFailed");
					}
					
					// TODO: add valid username check here
					
					if (!present(requestedGroup.getDescription())) {
						// TODO: add form error for field
						this.errors.reject("settings.group.error.requestGroupFailed");
					}
					if (!present(requestedGroup.getGroupRequest().getReason())) {
						// TODO: add form error for field
						this.errors.reject("settings.group.error.requestGroupFailed");
					}
					
					// TODO: add field for email?
					
					// TODO: add spammer check here
					
					// TODO: add check if username is already in the system
					
					if (!this.errors.hasErrors()) {
						// set the username and create the request
						requestedGroup.getGroupRequest().setUserName(loginUser.getName());
						this.logic.createGroup(requestedGroup);
					}
					
					return new ExtendedRedirectView("/");
				}
			}
		}

		return Views.GROUPREQUEST;
	}
	
	/**
	 * @return the current command
	 */
	@Override
	public GroupRequestCommand instantiateCommand() {
		final GroupRequestCommand command = new GroupRequestCommand();
		command.setGroup(new Group());
		return command;
	}


	
	
	
	
	
	
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
	
	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}
	
	@Override
	public boolean isValidationRequired(GroupRequestCommand command) {
		// FIXME: why?
		return command.getContext().getLoginUser().isSpammer()
				&& command.getContext().getLoginUser().getToClassify() == 0;
	}

	@Override
	public Validator<GroupRequestCommand> getValidator() {
		return new Validator<GroupRequestCommand>() {

			@Override
			public boolean supports(Class<?> clazz) {
				return SettingsViewCommand.class.equals(clazz);
			}

			@Override
			public void validate(Object target, Errors errors) {
				Assert.notNull(target);
				final SettingsViewCommand command = (SettingsViewCommand) target;
				
				ValidationUtils.invokeValidator(new GroupValidator(), command.getGroup(), errors);
			}
		};
	}


}
