package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.MailUtils;
import org.bibsonomy.webapp.command.actions.JoinGroupCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
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
public class JoinGroupController implements ErrorAware, MinimalisticController<JoinGroupCommand>, ValidationAwareController<JoinGroupCommand>, RequestAware, Validator<JoinGroupCommand> {
	
	private Captcha captcha;
	private RequestLogic requestLogic;
	private Errors errors = null;
	private LogicInterface logic;
	private MailUtils mailUtils;
	/**
	 * Path to login page for redirect if user not logged in
	 */
	private String loginPath;
	/**
	 * Path to this controller for referrer to login page
	 */
	private String controllerPath;
	
	/**
	 * Constructor.
	 */
	public JoinGroupController() {
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		/*
		 * here: check for binding errors
		 */
		this.errors = errors;
	}

	/** Give this controller an instance of {@link Captcha}.
	 * 
	 * @param captcha
	 */
	@Required
	public void setCaptcha(Captcha captcha) {
		this.captcha = captcha;
	}

	/** The logic needed to access the request
	 * @param requestLogic 
	 */
	@Override
	@Required
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/** Injects an instance of the MailUtils to send registration success mails.
	 * @param mailUtils
	 */
	@Required
	public void setMailUtils(MailUtils mailUtils) {
		this.mailUtils = mailUtils;
	}

	/**
	 * @param logic
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param loginPath
	 */
	public void setLoginPath(String loginPath) {
		this.loginPath = loginPath;
	}

	/**
	 * @return String that represents the path to the login page including the referrers request
	 * parameter and '='.
	 * 
	 * '/login?referer='
	 */
	public String getLoginPath() {
		return loginPath;
	}

	/**
	 * @param controllerPath
	 */
	public void setControllerPath(String controllerPath) {
		this.controllerPath = controllerPath;
	}

	/**
	 * @return Path to this controller
	 */
	public String getControllerPath() {
		return controllerPath;
	}

	@Override
	public JoinGroupCommand instantiateCommand() {
		return new JoinGroupCommand();
	}

	@Override
	public View workOn(JoinGroupCommand command) {
		//check user logged in
		if(command.getContext().isUserLoggedIn() == false) {
			String redirectURI;
			try {
				redirectURI = loginPath + URLEncoder.encode(controllerPath + "?" + command.getContext().getQueryString(), "UTF-8");
				return new ExtendedRedirectView(redirectURI );
			} catch (UnsupportedEncodingException ex) {
				throw new InternServerException(ex.getMessage());
			}
		}
		//check user is spammer
		if(command.getContext().getLoginUser().isSpammer()) {
			errors.reject("joinGroup.spammerError");
		}
		//on errors return to form
		if(errors.hasErrors()) {
			command.setCaptchaHTML(captcha.createCaptchaHtml(requestLogic.getLocale()));
			return Views.JOIN_GROUP;
		}
		//success now
		User group = logic.getUserDetails(command.getGroup());
		mailUtils.sendJoinGroupRequest(group, command.getContext().getLoginUser(), command.getReason(), requestLogic.getLocale());
		return null;
	}

	@Override
	public boolean isValidationRequired(JoinGroupCommand command) {
		RequestWrapperContext context = command.getContext();
		return context.isUserLoggedIn() && context.getLoginUser().isSpammer() == false;
	}

	@Override
	public Validator<JoinGroupCommand> getValidator() {
		return this;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return JoinGroupCommand.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		JoinGroupCommand command = (JoinGroupCommand) target;
		Assert.notNull(command.getGroup());
		if(present(command.getRecaptcha_response_field()) == false) {
			errors.rejectValue("recaptcha_response_field", "error.field.valid.captcha");
		} else {
			CaptchaResponse resp = captcha.checkAnswer(command.getRecaptcha_challenge_field(), command.getRecaptcha_response_field(), requestLogic.getHostInetAddress());
			if(resp == null) throw new InternServerException("error.captcha");
			if(resp.isValid() == false) errors.rejectValue("recaptcha_response_field", "error.field.valid.captcha");
			else {
				String errorMessage = resp.getErrorMessage();
				if(errorMessage != null) errors.reject(errorMessage);
			}
		}
	}

}
