/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupCreationMode;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupRequest;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.MailUtils;
import org.bibsonomy.webapp.command.GroupRequestCommand;
import org.bibsonomy.webapp.controller.actions.UserRegistrationController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.captcha.Captcha;
import org.bibsonomy.webapp.util.captcha.CaptchaUtil;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.validation.GroupRequestValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

/**
 * @author Mario Holtmueller
 */
public class GroupRequestController implements ValidationAwareController<GroupRequestCommand>, ErrorAware, RequestAware {
	private static final Log log = LogFactory.getLog(UserRegistrationController.class);

	private Errors errors;
	private LogicInterface logic;
	private LogicInterface adminLogic;
	private MailUtils mailer;
	private GroupCreationMode groupCreationMode;
	private RequestLogic requestLogic;
	private Captcha captcha;

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
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedNoticeException("please log in", "error.general.login");
		}

		final User loginUser = context.getLoginUser();

		if (loginUser.isSpammer()) {
			this.errors.reject("requestGroup.spammerError");
			return Views.ERROR;
		}

		/*
		 * check the ckey
		 */
		if (!context.isValidCkey()) {
			this.errors.reject("error.field.valid.ckey");
		}
		
		/*
		 * check captcha; an error is added if it fails.
		 */
		CaptchaUtil.checkCaptcha(this.captcha, this.errors, log, command.getRecaptcha_challenge_field(), command.getRecaptcha_response_field(), this.requestLogic.getHostInetAddress());
		
		final Group requestedGroup = command.getGroup();

		/*
		 * check if group name already exists
		 */
		final String groupName = requestedGroup.getName();
		if (!this.errors.hasErrors()) {
			// we use the admin logic to get all users even deleted ones
			final List<User> pendingUserList = this.adminLogic.getUsers(null, GroupingEntity.PENDING, groupName, null, null, null, null, null, 0, 1);
			if (this.adminLogic.getUserDetails(groupName).getName() != null || present(pendingUserList)) {
				// group name still exists, another one is required
				this.errors.rejectValue("group.name", "error.field.duplicate.group.name");
			}
		}

		if (this.errors.hasErrors()) {
			command.setCaptchaHTML(this.captcha.createCaptchaHtml(this.requestLogic.getLocale()));
			return Views.GROUPREQUEST;
		}
		
		final User groupAdmin;
		
		// check if group was requested by an admin and she/he specified a group admin
		final GroupRequest groupRequest = requestedGroup.getGroupRequest();
		final boolean loggedInUserIsAdmin = Role.ADMIN.equals(loginUser.getRole());
		if (loggedInUserIsAdmin) {
			final String groupAdminName = groupRequest.getUserName();
			if (present(groupAdminName)) {
				groupAdmin = this.logic.getUserDetails(groupAdminName);
				if (!UserUtils.isExistingUser(groupAdmin)) {
					this.errors.reject("requestGroup.userNotExistError", new Object[]{groupAdmin}, "There's no user with the name {0}.");
					return Views.ERROR;
				}
			} else {
				groupAdmin = loginUser;
			}
		} else {
			groupAdmin = loginUser;
		}
		/*
		 * prepare the group request object
		 */
		groupRequest.setUserName(groupAdmin.getName());
		final boolean activateGroup = loggedInUserIsAdmin || GroupCreationMode.AUTOMATIC.equals(this.groupCreationMode);
		if (activateGroup) {
			groupRequest.setReason("");
		}
		
		this.logic.createGroup(requestedGroup);
		
		if (activateGroup) {
			this.adminLogic.updateGroup(requestedGroup, GroupUpdateOperation.ACTIVATE, null);
			this.mailer.sendGroupActivationNotification(requestedGroup, groupAdmin, this.requestLogic.getLocale());
			command.setMessage("success.group.created", Collections.singletonList(groupName));
		} else {
			this.mailer.sendGroupRequest(requestedGroup);
			command.setMessage("success.groupRequest.sent", Collections.singletonList(groupName));
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
	 * @param logic
	 *            the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param adminLogic
	 *            the adminLogic to set
	 */
	public void setAdminLogic(final LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	/**
	 * @param mailer
	 *            the mailer to set
	 */
	public void setMailer(final MailUtils mailer) {
		this.mailer = mailer;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	@Override
	public boolean isValidationRequired(final GroupRequestCommand command) {
		return true;
	}

	@Override
	public Validator<GroupRequestCommand> getValidator() {
		return new GroupRequestValidator(this.groupCreationMode);
	}

	/**
	 * @param groupCreationMode
	 */
	public void setGroupCreationMode(final GroupCreationMode groupCreationMode) {
		this.groupCreationMode = groupCreationMode;
	}

	@Override
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * Give this controller an instance of {@link Captcha}.
	 *
	 * @param captcha
	 */
	@Required
	public void setCaptcha(final Captcha captcha) {
		this.captcha = captcha;
	}
}
