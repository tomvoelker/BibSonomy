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

import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.enums.GroupCreationMode;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.MailUtils;
import org.bibsonomy.webapp.command.GroupRequestCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.validation.GroupRequestValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;


/**
 * @author Mario Holtmueller
 */
public class GroupRequestController implements ValidationAwareController<GroupRequestCommand>, ErrorAware, RequestAware {

	private Errors errors = null;
	private LogicInterface logic;
	private LogicInterface adminLogic;
	private MailUtils mailer;
	private GroupCreationMode groupCreationMode;
	private RequestLogic requestLogic;
	
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
		
		final User loginUser = context.getLoginUser();
		
		if (loginUser.isSpammer()) {
			errors.reject("requestGroup.spammerError");
			return Views.ERROR;
		}
		
		/*
		 * check the ckey
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
		}
		
		final Group requestedGroup = command.getGroup();
		
		/*
		 * check if group name already exists
		 */
		if (this.errors.hasErrors()) {
			return Views.GROUPREQUEST;
		}
		
		final String groupName = requestedGroup.getName();
		// we use the admin logic to get all users even deleted ones
		final List<User> pendingUserList = this.adminLogic.getUsers(null, GroupingEntity.PENDING, groupName, null, null, null, null, null, 0, 1);
		if (this.adminLogic.getUserDetails(groupName).getName() != null || present(pendingUserList)) {
			// group name still exists, another one is required
			this.errors.rejectValue("group.name", "error.field.duplicate.group.name");
		}
		
		if (this.errors.hasErrors()) {
			return Views.GROUPREQUEST;
		}
		
		requestedGroup.getGroupRequest().setUserName(loginUser.getName());
		this.logic.createGroup(requestedGroup);
		
		switch (this.groupCreationMode) {
			case AUTOMATIC:
				this.adminLogic.updateGroup(requestedGroup, GroupUpdateOperation.ACTIVATE, null);
				this.mailer.sendGroupActivationNotification(requestedGroup, loginUser, this.requestLogic.getLocale());
				command.setMessage("success.group.activation", Collections.singletonList(groupName));
				break;
			case REQUESTEDBASED:
				this.mailer.sendGroupRequest(requestedGroup);
				command.setMessage("success.groupRequest.sent", Collections.singletonList(groupName));
				break;
			default:
				break;
		}
		
		return Views.SUCCESS;
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
	
	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	/**
	 * @param mailer the mailer to set
	 */
	public void setMailer(MailUtils mailer) {
		this.mailer = mailer;
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
		return true;
	}

	@Override
	public Validator<GroupRequestCommand> getValidator() {
		return new GroupRequestValidator();
	}

	public void setGroupCreationMode(GroupCreationMode groupCreationMode) {
		this.groupCreationMode = groupCreationMode;
	}

	@Override
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}
}
