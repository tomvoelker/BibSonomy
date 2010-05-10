package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
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

import filters.InitUserFilter;

/**
 * This controller handles the registration of users via Ldap 
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
			throw new AccessDeniedException("error.method_not_allowed");
		}

		log.info("errors"+errors.toString());
		
		if (errors.hasErrors()) {
			log.info("an error occoured: " + errors.toString());
			
			if (command.getStep() == 3) {
				return Views.REGISTER_USER_LDAP_FORM;
			}
			
			return Views.REGISTER_USER_LDAP;
		}

		/*
		 * Registration steps
		 */
		if (command.getStep() == 1) {

			log.debug("step 1: fill ldap form (username/password)");
			/*
			 * show form to enter Ldap
			 */
			return Views.REGISTER_USER_LDAP;
		} else if (command.getStep() == 2) {
			Views returnView = Views.REGISTER_USER_LDAP;
			log.debug("step 2: show ldap registration form");

			String registerUserName = null;
			String registerUserPassword = null;
			
			
			// user registration over login process
			if (null != requestLogic.getSessionAttribute(InitUserFilter.REQ_ATTRIB_LOGIN_USER)) {
				log.info("got Userdata via session");
				// retrieve username and password from session (set in UserLoginController)
				registerUserName = (String) requestLogic.getSessionAttribute(InitUserFilter.REQ_ATTRIB_LOGIN_USER);
				registerUserPassword = (String) requestLogic.getSessionAttribute(InitUserFilter.REQ_ATTRIB_LOGIN_USER_PASSWORD);
			} else // user registration over registration process  
			if (null != requestLogic.getParameter("registerUser.name")) {
				log.info("got Userdata via http-request");
				registerUserName = requestLogic.getParameter("registerUser.name");
				registerUserName = requestLogic.getParameter("registerUser.password");
			}
				
			/*
			 * check, if ldap user id already exists in ldap user table
			 */
			if (null != logic.getUsernameByLdapUserId(registerUserName)) {
				/*
				 * yes -> user must choose another name
				 */
				errors.rejectValue("registerUser.name", "error.field.duplicate.user.name");
			} 
			

			if (errors.hasErrors()) {
				/*
				 * Generate HTML to show captcha.
				 */
				log.debug("step 2: hasErrors() -> redirecting to Views.REGISTER_USER_LDAP (step 1)");
				return Views.REGISTER_USER_LDAP;
			}
			

			// check credentials
			Ldap ldap = new Ldap();
			LdapUserinfo ldapUserinfo = new LdapUserinfo();
			log.info("Trying to login user " + registerUserName + " via LDAP");
			ldapUserinfo = ldap.checkauth(registerUserName, registerUserPassword);

			if (null == ldapUserinfo) {
				log.info("Login check for registering failed for user " + registerUserName + " via LDAP");
				// if login failed, return to step 1 - show REGISTER_USER_LDAP

				// set some error messages
				errors.rejectValue("loginmessage", "error.login.failed");
				returnView = Views.REGISTER_USER_LDAP;
			} else {
				// if login was successful, insert ldap data to command
				log.info("Login check for registering succeeded for user " + registerUserName + " via LDAP");
				System.out.println("UserLDAPRegistration:: " + ldapUserinfo.toString());

				// check if username is already used and try another
				String newName = ldapUserinfo.getSureName().toLowerCase();
				int tryCount = 0;
				log.info("try existence of username: "+newName);
				while ((newName.equalsIgnoreCase(adminLogic.getUserDetails(newName).getName())) && (tryCount<101)) {
					try {
						if (tryCount == 0) {
							// try first character of forename concatenated with surename
							// bugs bunny => bbunny
							newName = ldapUserinfo.getFirstName().substring(0, 1).toLowerCase().concat(newName);
						} else if (tryCount == 100) {
							// now use first character of fore- and first two characters of surename concatenated with ldap user id 
							// bugs bunny => bbu01234567
							newName = newName.substring(0, 3).concat(ldapUserinfo.getUserId());
						} else {
							// try first character of forename concatenated with surename concatenated with current number
							// bugs bunny => bbunnyX where X is between 1 and 9
							if (tryCount==1) {
								// add trycount to newName
								newName = newName.concat(Integer.toString(tryCount));
							} else { 
								// replace last two characters of string with trycount
								newName = newName.substring(0, newName.length()-Integer.toString(tryCount-1).length()).concat(Integer.toString(tryCount));
							}
						}
						log.info("try existence of username: "+newName+" ("+tryCount+")");
						tryCount++;
					}
					catch (IndexOutOfBoundsException ex) {
						/*
						 * if some substring values are out of range, catch exception and use surename
						 */
						newName = ldapUserinfo.getSureName().toLowerCase();
						tryCount = 99;
					}
					
				}

				command.getRegisterUser().setName(newName);
				command.getRegisterUser().setEmail(ldapUserinfo.getEmail());
				command.getRegisterUser().setRealname(ldapUserinfo.getFirstName() + " " + ldapUserinfo.getSureName());
				// command.getRegisterUser().setGender(ldapUserinfo.);
				command.getRegisterUser().setPlace(ldapUserinfo.getLocation());
				command.getRegisterUser().setLdapId(ldapUserinfo.getUserId());
				command.getRegisterUser().setPassword(ldapUserinfo.getPasswordHashMd5Hex());
				returnView = Views.REGISTER_USER_LDAP_FORM;
			}

			return returnView;
		} else if (command.getStep() == 3) {

			log.debug("step 3: complete ldap registration");
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
			if (null != registerUser.getName() && null != logic.getUserDetails(registerUser.getName()).getName() ) {
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
				return Views.REGISTER_USER_LDAP_FORM;
			}

			log.info("validation passed with " + errors.getErrorCount() + " errors, proceeding to access database");

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
			 * *depreciated*
			 * 
			 * TODO: choose better random pw
			 */
//			String password = StringUtils.getMD5Hash(registerUser.getName() + "LDAP_");
//			String password = StringUtils.getMD5Hash("we_do_not_need_this_password_while_using_ldap_server");
//			String password = registerUser.getPassword();
//			registerUser.setPassword(password);

			/*
			 * create user in DB
			 */
			logic.createUser(registerUser);

			
			/*
			 * log user into system
			 */
			cookieLogic.addUserCookie(registerUser.getName(), registerUser.getPassword());
			
			/*
			 * present the success view
			 */
			return new ExtendedRedirectView(successRedirect);
		}

		return Views.REGISTER_USER_LDAP;
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