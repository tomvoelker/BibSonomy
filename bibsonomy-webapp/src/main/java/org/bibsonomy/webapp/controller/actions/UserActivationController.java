package org.bibsonomy.webapp.controller.actions;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.MailUtils;
import org.bibsonomy.webapp.command.actions.UserActivationCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

/**
 * @author Clemens Baier
 * @version $Id$
 */
public class UserActivationController implements MinimalisticController<UserActivationCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(UserActivationController.class);

	private RequestLogic requestLogic;
	private LogicInterface logic;
	private Errors errors;
	private MailUtils mailUtils;
	
	private String successRedirect = "/activate_success";

	@Override
	public UserActivationCommand instantiateCommand() {
		return new UserActivationCommand();
	}

	@Override
	public View workOn(UserActivationCommand command) {
		final RequestWrapperContext context = command.getContext();
		command.setPageTitle("activation");
		
		/*
		 * user must not be logged in
		 */
		if (context.isUserLoggedIn()) {
			throw new AccessDeniedException("error.method_not_allowed");
		}
		
		final String inetAddress = requestLogic.getInetAddress();
		final Locale locale = requestLogic.getLocale();
		
		final String activationCode = requestLogic.getParameter("activationCode");
		final List<User> list = logic.getUsers(null, GroupingEntity.PENDING, null, null, null, null, null, activationCode, 0, Integer.MAX_VALUE);
		User pendingUser = null;
		if (list.size() == 0) {
			errors.reject("error.illegal_activation_code");
		} else {
			log.debug("trying to activate user with code '" + activationCode + "'");

			pendingUser = list.get(0);
			/*
			 * check, if activation code is invalid.
			 * 
			 * now < registration_date + 24h
			 */
			final Calendar now = Calendar.getInstance();
			final Calendar activationCodeExpirationDate = Calendar.getInstance();
			activationCodeExpirationDate.setTime(pendingUser.getRegistrationDate());
			activationCodeExpirationDate.add(Calendar.HOUR, 24);
			if (!now.before(activationCodeExpirationDate)) {
				errors.reject("error.activation_code_expired");
			}
		}

		/*
		 * if there are errors, show them
		 */
		if (errors.hasErrors()) {
			return Views.ERROR;
		}
		
		logic.updateUser(pendingUser, UserUpdateOperation.ACTIVATE);
		// user should be activated now
		
		/*
		 * send activation confirmation mail
		 */
		try {
			mailUtils.sendRegistrationMail(pendingUser.getName(), pendingUser.getEmail(), pendingUser.getActivationCode(), inetAddress, locale);
		} catch (final Exception e) {
			log.error("Could not send activation confirmation mail for user " + pendingUser.getName(), e);
		}
		
		return new ExtendedRedirectView(successRedirect);
	}

	/**
	 * @param logic
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @return errors
	 */
	public Errors getErrors() {
		return this.errors;
	}

	/**
	 * @param errors
	 */
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * @param requestLogic
	 */
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}
	
	/** After successful activation, the user is redirected to this page.
	 * @param successRedirect
	 */
	public void setSuccessRedirect(String successRedirect) {
		this.successRedirect = successRedirect;
	}

	/** Injects an instance of the MailUtils to send activation success mails.
	 * @param mailUtils
	 */
	@Required
	public void setMailUtils(MailUtils mailUtils) {
		this.mailUtils = mailUtils;
	}
}