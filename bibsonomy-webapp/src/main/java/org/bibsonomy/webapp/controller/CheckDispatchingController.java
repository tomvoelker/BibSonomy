package org.bibsonomy.webapp.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.command.CaptchaResponseCommand;
import org.bibsonomy.webapp.command.ContextCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.captcha.Captcha;
import org.bibsonomy.webapp.util.captcha.CaptchaResponse;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.validation.Errors;

/**
 * Abstract Controller that can check user logged in, ckey and captcha response if desired.
 * 
 * @author schwass
 * @version $Id$
 * @param <CT> Command Type
 */
public abstract class CheckDispatchingController<CT extends ContextCommand> implements MinimalisticController<CT>, ErrorAware, RequestAware {

	protected static final Log log = LogFactory.getLog(CheckDispatchingController.class);
	/**
	 * A check that should be done by this controller.
	 * 
	 * @author schwass
	 *
	 */
	protected abstract class Check {
		
		protected abstract boolean execute(CT command, Errors errors);
		protected abstract View onCheckFailed(CT command, Errors errors);
	}
	/**
	 * Checks if the user is logged in.
	 * 
	 * @author schwass
	 *
	 */
	protected class UserLoggedInCheck extends Check {
		/**
		 * path to the login page for redirecting not logged in user
		 */
		private String loginPage;
		/**
		 * send as referrer to the login page
		 */
		private String urlPath;
		
		public UserLoggedInCheck() {
			
		}

		@Override
		protected boolean execute(CT command, Errors errors) {
			return command.getContext().isUserLoggedIn();
		}

		@Override
		protected View onCheckFailed(CT command, Errors errors) {
			final RequestWrapperContext context = command.getContext();
			errors.reject("error.general.login");
			try {
				return new ExtendedRedirectView(
						loginPage + URLEncoder.encode(
								urlPath + context.getQueryString(), "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				return new ExtendedRedirectView(loginPage + urlPath + context.getQueryString());
			}
		}
		
	}
	/**
	 * Checks if CKey is valid.
	 * 
	 * @author schwass
	 *
	 */
	protected class CKeyCheck extends Check {

		@Override
		protected boolean execute(CT command, Errors errors) {
			return command.getContext().isValidCkey();
		}

		@Override
		protected View onCheckFailed(CT command, Errors errors) {
			errors.reject("error.field.valid.ckey");
			return failureView;
		}
		
	}
	/**
	 * Checks captcha (creates captcha html).
	 * 
	 * @author schwass
	 *
	 */
	protected class CaptchaCheck extends Check {
		
		Captcha captcha;
		RequestLogic requestLogic;
		
		public CaptchaCheck() {
			
		}

		@Override
		protected boolean execute(CT command, Errors errors) {
			return true;
		}

		@Override
		protected View onCheckFailed(CT command, Errors errors) {
			return null;
		}
		private String createCaptchaHTML() {
			return captcha.createCaptchaHtml(requestLogic.getLocale());
		}
		
	}
	/**
	 * Checks captcha response and creates captcha html.
	 * 
	 * @author schwass
	 *
	 */
	protected class CaptchaResponseCheck extends CaptchaCheck {
		
		public CaptchaResponseCheck() {
			
		}

		@Override
		protected boolean execute(CT command, Errors errors) {
			CaptchaResponseCommand captchaCommand = (CaptchaResponseCommand) command;
			final CaptchaResponse res = captcha.checkAnswer(captchaCommand.getChallenge(), captchaCommand.getResponse(), requestLogic.getHostInetAddress());
			if(res == null) {
				errors.reject("error.captcha");
				return true;
			}
			final boolean result = res.isValid();
			//untested: can this case occur: valid response, but existing error message?
			String errorMessage = res.getErrorMessage();
			if(errorMessage != null)errors.reject("error.captcha");
			return result;
		}

		@Override
		protected View onCheckFailed(CT command, Errors errors) {
			return failureView;
		}
		
	}
	/**
	 * Checks if errors exists.
	 * 
	 * @author schwass
	 *
	 */
	protected class ErrorsExistsCheck extends Check {

		public ErrorsExistsCheck() {
		}

		@Override
		protected boolean execute(CT command, Errors errors) {
			return errors.hasErrors();
		}

		@Override
		protected View onCheckFailed(CT command, Errors errors) {
			return failureView;
		}
		
	}
	
	private final List<Check> checks = new ArrayList<Check>();
	
	private UserLoggedInCheck userLoggedInCheck;
	private CaptchaCheck captchaCheck;
	
	private Errors errors = null;
	
	private final View successView;
	private final View failureView;
	
	/**
	 * Constructor
	 */
	public CheckDispatchingController() {
		this(null, null);
	}
	/**
	 * Constructor for extending classes.
	 * 
	 * @param successView View that gets returned on success
	 * @param failureView View that gets returned on any failure
	 */
	protected CheckDispatchingController(
			View successView,
			View failureView) {
		this.successView = successView;
		this.failureView = failureView;
	}
	/**
	 * Add a check.
	 * 
	 * @param check The check to be added
	 */
	protected void addCheck(Check check) {
		checks.add(check);
	}
	/**
	 * Add a UserLoggedInCheck.
	 * 
	 * @param userLoggedInCheck
	 */
	protected void addUserLoggedInCheck(UserLoggedInCheck userLoggedInCheck) {
		checks.add(userLoggedInCheck);
		this.userLoggedInCheck = userLoggedInCheck;
	}
	/**
	 * Add a captcha check.
	 * 
	 * @param captchaCheck
	 */
	protected void addCaptchaCheck(CaptchaCheck captchaCheck) {
		checks.add(captchaCheck);
		this.captchaCheck = captchaCheck;
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
	
	/**
	 * @param captcha
	 */
	public void setCaptcha(Captcha captcha) {
		if(captchaCheck == null)return;
		captchaCheck.captcha = captcha;
	}

	/**
	 * @param requestLogic
	 */
	@Override
	public void setRequestLogic(RequestLogic requestLogic) {
		if(captchaCheck == null)return;
		captchaCheck.requestLogic = requestLogic;
	}

	/**
	 * @param loginPage
	 */
	public void setLoginPage(String loginPage) {
		if(userLoggedInCheck == null)return;
		userLoggedInCheck.loginPage = loginPage;
	}

	/**
	 * The path specified will be send as referrer to the login page, when redirected there.
	 * 
	 * @param urlPath url path to this controller.
	 */
	public void setUrlPath(String urlPath) {
		if(userLoggedInCheck == null)return;
		userLoggedInCheck.urlPath = urlPath;
	}

	@Override
	public final View workOn(CT command) {
		
		for(Check check : checks) {
			if(check.execute(command, errors) == false) return check.onCheckFailed(command, errors);
		}
		
		return onSuccess(command);
	}
	/**
	 * Gets called when all tests are passed.
	 * 
	 * @param command Command to work on
	 * @return successView specified.
	 */
	protected View onSuccess(CT command) {
		return successView;
	}
	/**
	 * Creates captcha html. Note that a CaptchaCeck must be added.
	 * 
	 * @return captcha html
	 */
	protected final String createCaptchaHTML() {
		if(captchaCheck == null)return null;
		return captchaCheck.createCaptchaHTML();
	}

}
