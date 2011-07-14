package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Group;
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
import org.bibsonomy.webapp.util.captcha.CaptchaResponse;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author schwass
 * @version $Id$
 */
public class JoinGroupController implements ErrorAware, ValidationAwareController<JoinGroupCommand>, RequestAware, Validator<JoinGroupCommand> {
	
	private Captcha captcha;
	private RequestLogic requestLogic;
	private Errors errors = null;
	private LogicInterface logic;
	private LogicInterface adminLogic;
	private MailUtils mailUtils;
	
	private String denieUserRedirectURI;
	
	/**
	 * maximum length for reason input.
	 */
	private int reasonMaxLen;

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
	 * @param denieUserRedirectURI the denieUserRedirectURI to set
	 */
	public void setDenieUserRedirectURI(final String denieUserRedirectURI) {
		this.denieUserRedirectURI = denieUserRedirectURI;
	}

	/**
	 * @param reasonMaxLen the reasonMaxLen to set
	 */
	public void setReasonMaxLen(final int reasonMaxLen) {
		this.reasonMaxLen = reasonMaxLen;
	}

	@Override
	public JoinGroupCommand instantiateCommand() {
		return new JoinGroupCommand(this.reasonMaxLen);
	}

	@Override
	public View workOn(final JoinGroupCommand command) {
		final User loginUser = command.getContext().getLoginUser();
		// check user logged in
		if (!command.getContext().isUserLoggedIn()) {
			throw new org.springframework.security.access.AccessDeniedException("please log in");
		}
		
		/* 
		 * deny join request action
		 */
		if (present(command.getDeniedUser())) {
			return this.workOnDeny(command, loginUser);
		}
		
		// check if user is already in this group
		if (loginUser.getGroups().contains(new Group(command.getGroup()))) {
			errors.reject("joinGroup.already.member.error");
		}
		
		// check user is spammer
		if (loginUser.isSpammer()) {
			errors.reject("joinGroup.spammerError");
		}
		
		// on errors return to form
		if (errors.hasErrors()) {
			command.setCaptchaHTML(captcha.createCaptchaHtml(requestLogic.getLocale()));
			return Views.JOIN_GROUP;
		}
		
		// success now
		final User group = logic.getUserDetails(command.getGroup());
		mailUtils.sendJoinGroupRequest(group, loginUser, command.getReason(), requestLogic.getLocale());
		command.setGroupObj(logic.getGroupDetails(command.getGroup()));
		
		return Views.JOINGROUPREQUEST_SUCCESS;
	}

	private View workOnDeny(final JoinGroupCommand command, final User loginUser) {
		// if ckey is not valid, don't annoy denied user with emails
		if (command.getContext().isValidCkey()) {
			mailUtils.sendJoinGroupDenied(loginUser, adminLogic.getUserDetails(command.getDeniedUser()), command.getReason(), requestLogic.getLocale());
		}
		return new ExtendedRedirectView(denieUserRedirectURI);
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
		Assert.notNull(command.getGroup()); // TODO: should add an error to errors
		if (present(command.getDeniedUser())) {
			if (!command.getContext().getLoginUser().getName().equals(command.getGroup())) throw new AccessDeniedException();
			// no further validation
			return;
		}
		
		if (present(command.getReason()) && command.getReason().length() > reasonMaxLen) {
			errors.rejectValue("reason", "error.field.valid.limit_exceeded", new Object[] {reasonMaxLen}, "Message is too long");
			command.setReason(command.getReason().substring(0, reasonMaxLen));
		}
		
		// FIXME: captcha checking; duplicate code EditPostController, PasswordReminderController, …
		if (!present(command.getRecaptcha_response_field())) {
			errors.rejectValue("recaptcha_response_field", "error.field.valid.captcha");
		} else {
			final CaptchaResponse resp = captcha.checkAnswer(command.getRecaptcha_challenge_field(), command.getRecaptcha_response_field(), requestLogic.getHostInetAddress());
			if(resp == null) throw new InternServerException("error.captcha");
			if(resp.isValid() == false) errors.rejectValue("recaptcha_response_field", "error.field.valid.captcha");
			else {
				final String errorMessage = resp.getErrorMessage();
				if(errorMessage != null) errors.reject(errorMessage);
			}
		}
	}

}
