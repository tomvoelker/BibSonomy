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
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.MailUtils;
import org.bibsonomy.webapp.command.actions.JoinGroupCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.captcha.Captcha;
import org.bibsonomy.webapp.util.captcha.CaptchaUtil;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

/**
 * Handles a user's request to join a group
 * 
 * @author schwass
 */
public class JoinGroupController implements ErrorAware, ValidationAwareController<JoinGroupCommand>, RequestAware, Validator<JoinGroupCommand> {
	
	private static final Log log = LogFactory.getLog(JoinGroupController.class);
	
	private Captcha captcha;
	private RequestLogic requestLogic;
	private Errors errors = null;
	private LogicInterface logic;
	private LogicInterface adminLogic;
	private MailUtils mailUtils;
	
	private String denyUserRedirectURI;
	
	/**
	 * maximum length for reason input.
	 */
	private int reasonMaxLen;

	@Override
	public JoinGroupCommand instantiateCommand() {
		return new JoinGroupCommand(this.reasonMaxLen);
	}

	
	@Override
	public View workOn(final JoinGroupCommand command) {
		// user logged in? 
		if (!command.getContext().isUserLoggedIn()) {
			throw new org.springframework.security.access.AccessDeniedException("please log in");
		}
		
		/*
		 * The user has three options and needs:
		 * * see join site: loginUser, group
		 * * join Group: loginUser, group, reason, ckey, captcha
		 * * denyUser: loginUser = group, reason, denyUser
		 */
		final User loginUser = command.getContext().getLoginUser();
		
		// get group details and check if present
		final String groupName = command.getGroup();
		final Group group = this.adminLogic.getGroupDetails(groupName, false);
		if (!present(group)) {
			// no group given => user did not click join on the group page
			errors.reject("error.field.valid.groupName");
			return Views.ERROR;
		}

		if (!group.isAllowJoin()) {
			// the group does not allow join requests
			errors.reject("joinGroup.joinRequestDisabled");
			return Views.ERROR;
		}
		
		final String reason = command.getReason();
		final String deniedUserName = command.getDeniedUser();
		
		// We can not check the ckey if "deny request" was chosen, since the deny
		// handle deny join request action
		if (present(deniedUserName)) {
			// TODO: (groups) remove
			/*
			 * We have a deny Request
			 */
			// check if loginUser is the group
			if (!groupName.equals(command.getContext().getLoginUser().getName())) {
				throw new AccessDeniedException("This action is only possible for a group. Please log in as a group!");
			}
			final User deniedUser = this.adminLogic.getUserDetails(deniedUserName);
			if (!present(deniedUser.getName())) {
				errors.reject("joinGroup.deny.noUser");
				return Views.ERROR;
			}
			mailUtils.sendJoinGroupDenied(loginUser.getName(), deniedUserName, deniedUser.getEmail(), reason, requestLogic.getLocale());
			return new ExtendedRedirectView(denyUserRedirectURI);
		}
		
		/*
		 * from here we assume, that the user has sent a join group request from the join group form
		 */
		final boolean joinRequest = command.isJoinRequest();
		
		// check if user is already has an open request ...
		if (loginUser.getPendingGroups().contains(group)) {
			errors.reject("joinGroup.already.request.error");
			return Views.ERROR;
		}
		// ... or is in this group
		if (loginUser.getGroups().contains(group)) {
			errors.reject("joinGroup.already.member.error");
			return Views.ERROR;
		}
		
		if (!present(reason)) {
			errors.rejectValue("reason", "error.field.required");
		}
		
		/*
		 * check if ckey is valid
		 */
		if (joinRequest && !command.getContext().isValidCkey()) {
			errors.reject("error.field.valid.ckey");
		}
		
		// check user is spammer
		if (loginUser.isSpammer()) {
			// user is a spammer => cannot use this page
			errors.reject("joinGroup.spammerError");
			return Views.ERROR;
		}
		
		/*
		 * check captacha; an error is added if it fails.
		 */
		CaptchaUtil.checkCaptcha(this.captcha, this.errors, log, command.getRecaptcha_challenge_field(), command.getRecaptcha_response_field(), this.requestLogic.getHostInetAddress());
		
		if (errors.hasErrors()) {
			command.setCaptchaHTML(captcha.createCaptchaHtml(requestLogic.getLocale()));
			return Views.JOIN_GROUP;
		}
		
		// user is allowed to state join request and group exists => execute request
		
		// send a mail to all administrators of the group
		for (final GroupMembership ms : group.getMemberships()) {
			if (ms.getGroupRole().equals(GroupRole.ADMINISTRATOR)) {
				final User groupAdminUser = ms.getUser();
				final String groudAdminUserMail = this.adminLogic.getUserDetails(groupAdminUser.getName()).getEmail();
				mailUtils.sendJoinGroupRequest(group.getName(), groudAdminUserMail, loginUser, command.getReason(), requestLogic.getLocale());
			}
		}
		
		// insert the request
		final GroupMembership gms = new GroupMembership(loginUser, GroupRole.USER, command.isUserSharedDocuments());
		this.logic.updateGroup(group, GroupUpdateOperation.ADD_REQUESTED, gms);

		command.setMessage("success.joinGroupRequest.sent", Collections.singletonList(groupName));
		return Views.SUCCESS;
	}

	@Override
	public boolean isValidationRequired(final JoinGroupCommand command) {
		final RequestWrapperContext context = command.getContext();
		return context.isUserLoggedIn() && !context.getLoginUser().isSpammer();
	}

	@Override
	public Validator<JoinGroupCommand> getValidator() {
		return this;
	}

	@Override
	public boolean supports(final Class<?> clazz) {
		return JoinGroupCommand.class.equals(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		final JoinGroupCommand command = (JoinGroupCommand) target;

		// check length
		if (present(command.getReason()) && command.getReason().length() > reasonMaxLen) {
			errors.rejectValue("reason", "error.field.valid.limit_exceeded", new Object[] {reasonMaxLen}, "Message is too long");
			command.setReason(command.getReason().substring(0, reasonMaxLen));
		}
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * Give this controller an instance of {@link Captcha}.
	 * @param captcha
	 */
	@Required
	public void setCaptcha(final Captcha captcha) {
		this.captcha = captcha;
	}

	/** The logic needed to access the request
	 * @param requestLogic 
	 */
	@Override
	@Required
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/** Injects an instance of the MailUtils to send registration success mails.
	 * @param mailUtils
	 */
	@Required
	public void setMailUtils(final MailUtils mailUtils) {
		this.mailUtils = mailUtils;
	}

	/**
	 * @param logic
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
	
	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(final LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	/**
	 * @param denyUserRedirectURI the denieUserRedirectURI to set
	 */
	public void setDenyUserRedirectURI(final String denyUserRedirectURI) {
		this.denyUserRedirectURI = denyUserRedirectURI;
	}

	/**
	 * @param reasonMaxLen the reasonMaxLen to set
	 */
	public void setReasonMaxLen(final int reasonMaxLen) {
		this.reasonMaxLen = reasonMaxLen;
	}
}
