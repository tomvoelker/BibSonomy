package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.actions.UserLDAPRegistrationCommand;
import org.bibsonomy.webapp.util.CookieAware;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.auth.Ldap;
import org.bibsonomy.webapp.util.auth.LdapUserinfo;
import org.bibsonomy.webapp.validation.UserLDAPRegistrationValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * This controller handles the registration of users via Ldap (see
 * http://openid.net/)
 * 
 * @author Sven Stefani
 * @version $Id: UserLDAPRegistrationController.java,v 1.1 2009-12-07 10:08:38
 *          sven Exp $
 */
public class UserLDAPRegistrationController implements MinimalisticController<UserLDAPRegistrationCommand>, ErrorAware, ValidationAwareController<UserLDAPRegistrationCommand>, RequestAware, CookieAware {

	protected LogicInterface logic;
	protected LogicInterface adminLogic;
	private Errors errors = null;
	private RequestLogic requestLogic;
	private CookieLogic cookieLogic;
	// private Ldap ldapLogic;

	private String projectHome;

	/**
	 * After successful registration, the user is redirected to this page.
	 */
	private String successRedirect = "";

	private static final Log log = LogFactory.getLog(UserLDAPRegistrationController.class);

	public View workOn(UserLDAPRegistrationCommand command) {
		log.debug("workOn() called");

		command.setPageTitle("LDAP registration");

		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		/*
		 * Check user role
		 * 
		 * If user is logged in and not an admin: show error message
		 */
		if (context.isUserLoggedIn() && !Role.ADMIN.equals(loginUser.getRole())) {
			log.warn("User " + loginUser.getName() + " tried to access user registration without having role " + Role.ADMIN);
			throw new ValidationException("error.method_not_allowed");
		}

		if (errors.hasErrors()) {
			log.info("an error occoured: " + errors.toString());
			return Views.REGISTER_USER_LDAP;
		}

		/*
		 * Registration steps
		 */
		if (command.getStep() == 1) {

			log.debug("step 1: fill form (username/password)");
			/*
			 * show form to enter Ldap
			 */
			return Views.REGISTER_USER_LDAP;
		} else if (command.getStep() == 2) {
			Views returnView = Views.REGISTER_USER_LDAP;
			log.debug("step 2: show registration form");

			// check credentials
			Ldap ldap = new Ldap();
			LdapUserinfo ldapUserinfo = new LdapUserinfo();
			log.info("Trying to login user " + requestLogic.getParameter("registerUser.name") + " via LDAP");
			ldapUserinfo = ldap.checkauth(requestLogic.getParameter("registerUser.name"), requestLogic.getParameter("registerUser.password"));

			if (null == ldapUserinfo) {
				log.info("Login check for registering failed for user " + requestLogic.getParameter("registerUser.name") + " via LDAP");
				// if login failed, return to step 1 - show REGISTER_USER_LDAP

				// set some error messages
				errors.rejectValue("loginmessage", "error.login.failed");
				returnView = Views.REGISTER_USER_LDAP;
			} else {
				// if login was successful, insert ldap data to command
				log.info("Login check for registering succeeded for user " + requestLogic.getParameter("registerUser.name") + " via LDAP");
				System.out.println("UserLDAPRegistration:: " + ldapUserinfo.toString());

				command.getRegisterUser().setName(ldapUserinfo.getSureName().toLowerCase());
				command.getRegisterUser().setEmail(ldapUserinfo.getEmail());
				command.getRegisterUser().setRealname(ldapUserinfo.getFirstName() + " " + ldapUserinfo.getSureName());
				// command.getRegisterUser().setGender(ldapUserinfo.);
				command.getRegisterUser().setPlace(ldapUserinfo.getLocation());
				command.getRegisterUser().setLdapUid(ldapUserinfo.getUserId());
				returnView = Views.REGISTER_USER_LDAP_FORM;
			}

			return returnView;
		} else if (command.getStep() == 4) {

			log.debug("step 4: complete Ldap registration");
			/*
			 * complete registration process and save user to database
			 */

			/*
			 * Check cookies
			 * 
			 * Check, if user has cookies enabled (there should be at least a
			 * "JSESSIONID" cookie)
			 */
			if (!cookieLogic.containsCookies()) {
				errors.reject("error.cookies_required");
			}

			/*
			 * User which wants to register (form data)
			 */
			final User registerUser = command.getRegisterUser();

			/*
			 * Get the hosts IP address.
			 */
			final String inetAddress = requestLogic.getInetAddress();

			/*
			 * If user is an admin, he must provide a valid ckey!
			 */
			final boolean adminAccess = context.isUserLoggedIn() && Role.ADMIN.equals(loginUser.getRole());
			if (adminAccess && !context.isValidCkey()) {
				errors.reject("error.field.valid.ckey");
			}

			/*
			 * check, if user name already exists
			 */
			if (registerUser.getName() != null && logic.getUserDetails(registerUser.getName()).getName() != null) {
				/*
				 * yes -> user must choose another name
				 */
				errors.rejectValue("registerUser.name", "error.field.duplicate.user.name");
			}

			/*
			 * return to form until validation passes
			 */
			if (errors.hasErrors()) {
				/*
				 * Generate HTML to show captcha.
				 */
				return Views.REGISTER_USER_OPENID_PROVIDER_FORM;
			}

			log.debug("validation passed with " + errors.getErrorCount() + " errors, proceeding to access database");

			/*
			 * if the user is not logged in, we need an instance of the logic
			 * interface with admin access
			 */
			if (!context.isUserLoggedIn()) {
				this.logic = this.adminLogic;
			}

			/*
			 * set the full inet address of the user
			 */
			registerUser.setIPAddress(inetAddress);

			/*
			 * generate random password
			 * 
			 * TODO: choose better random pw
			 */
			String password = StringUtils.getMD5Hash(registerUser.getName() + "OPENID_");
			registerUser.setPassword(password);

			/*
			 * create user in DB
			 */
			logic.createUser(registerUser);

			/*
			 * log user into system
			 */
			// cookieLogic.addLdapCookie(registerUser.getName(),
			// command.getRegisterUser().getLdap(), registerUser.getPassword());
			// ldapLogic.extendLdapSession(requestLogic.getSession(),
			// command.getRegisterUser().getLdap());

			/*
			 * present the success view
			 */
			return new ExtendedRedirectView(successRedirect);
		}

		return Views.REGISTER_USER_OPENID;
	}

	public UserLDAPRegistrationCommand instantiateCommand() {
		final UserLDAPRegistrationCommand userLdapRegistrationCommand = new UserLDAPRegistrationCommand();
		/*
		 * add user to command
		 */
		userLdapRegistrationCommand.setRegisterUser(new User());
		return userLdapRegistrationCommand;
	}

	public Errors getErrors() {
		return this.errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	public Validator<UserLDAPRegistrationCommand> getValidator() {
		return new UserLDAPRegistrationValidator();
	}

	public boolean isValidationRequired(UserLDAPRegistrationCommand command) {
		return true;
	}

	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	public void setCookieLogic(CookieLogic cookieLogic) {
		this.cookieLogic = cookieLogic;
	}

	/**
	 * @param logic
	 *            logic interface
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param adminLogic
	 *            - an instance of the logic interface with admin access.
	 */
	@Required
	public void setAdminLogic(LogicInterface adminLogic) {
		Assert.notNull(adminLogic, "The provided logic interface must not be null.");
		this.adminLogic = adminLogic;
		/*
		 * Check, if logic has admin access.
		 */
		Assert.isTrue(Role.ADMIN.equals(this.adminLogic.getAuthenticatedUser().getRole()), "The provided logic interface must have admin access.");
	}

	/**
	 * @param ldapLogic
	 *            - an instance of the Ldap logic public void setLdapLogic(Ldap
	 *            ldapLogic) { this.ldapLogic = ldapLogic; }
	 */

	/**
	 * The base URL of the project.
	 * 
	 * @param projectHome
	 */
	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}

	/**
	 * After successful registration, the user is redirected to this page.
	 * 
	 * @param successRedirect
	 */
	public void setSuccessRedirect(String successRedirect) {
		this.successRedirect = successRedirect;
	}
}