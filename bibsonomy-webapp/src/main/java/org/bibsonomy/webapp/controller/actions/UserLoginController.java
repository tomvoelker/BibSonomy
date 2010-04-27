package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.actions.UserLDAPRegistrationCommand;
import org.bibsonomy.webapp.command.actions.UserLoginCommand;
import org.bibsonomy.webapp.exceptions.ServiceUnavailableException;
import org.bibsonomy.webapp.util.CookieAware;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.TeerGrube;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.auth.Ldap;
import org.bibsonomy.webapp.util.auth.LdapUserinfo;
import org.bibsonomy.webapp.util.auth.OpenID;
import org.bibsonomy.webapp.validation.UserLoginValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.openid4java.OpenIDException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import filters.InitUserFilter;

/** This controller handles the login of users. It provides the following 
 * authentication mechanisms:
 * <ul>
 * <li>OpenID</li>
 * <li>username + cleartext password as POST request</li>
 * <li>username + reminder password as POST request</li>
 * </ul>
 * 
 * <p>For other authentication mechanisms (X.509/LDAP, OpenID, HTTP Basic Auth, 
 * Cookie) have a look at {@link InitUserFilter} </p>
 * 
 * @author rja
 * @version $Id$
 */
public class UserLoginController implements MinimalisticController<UserLoginCommand>, ErrorAware, ValidationAwareController<UserLoginCommand>, RequestAware, CookieAware {
	private static final Log log = LogFactory.getLog(UserLoginController.class);

	
	protected LogicInterface adminLogic;
	private Errors errors = null;
	private RequestLogic requestLogic;
	private CookieLogic cookieLogic;
	private OpenID openIDLogic;

	private String projectHome;

	/**
	 * The max number of minutes, a password reminder is valid.
	 */
	private int maxMinutesPasswordReminderValid = 60;

	private TeerGrube grube;


	/** 
	 * Returns an instance of the command the controller handles.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	public UserLoginCommand instantiateCommand() {
		return new UserLoginCommand();
	}


	/** Main method which does the registration.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(java.lang.Object)
	 */
	public View workOn(UserLoginCommand command) {
		log.debug("workOn() called");

		command.setPageTitle("login");

		/*
		 * remember referer to send user back to the page she's coming from
		 */
		setReferer(command);

		/*
		 * catch response from OpenID provider
		 */
		final String referer = command.getReferer();
		if (requestLogic.getParameter("openid.identity") != null) {
			String openID = requestLogic.getParameter("openid.identity");

			/*
			 * if login successful then redirect to referer
			 */
			if(this.handleOpenIDLogin(openID)) {
				return new ExtendedRedirectView(referer);
			} 

			/*
			 * else to openID registration page
			 */
			return new ExtendedRedirectView("/registerOpenID");

		}

		/* Check cookies
		 * 
		 * Check, if user has cookies enabled (there should be at least a "JSESSIONID" cookie)
		 */
		if (!cookieLogic.containsCookies()) {
			errors.reject("error.cookies_required");
		}


		if (errors.hasErrors()) {
			log.debug(errors.getAllErrors().get(0).toString());
			//command.setLoginMethod("ldap");
			return Views.LOGIN;
		}

		/*
		 * extract username and password
		 */
		final String username = command.getUsername();
		final String password = command.getPassword();
		final String hashedPassword = StringUtils.getMD5Hash(password);


		/*
		 * OpenID used for authentication?
		 */
		final String openID = command.getOpenID();


		/*
		 * Get the hosts IP address.
		 */
		final String inetAddress = requestLogic.getInetAddress();


		/*
		 * Check, if the user (or IP) has to wait some time for another login try.
		 */
		handleWaiting(username, inetAddress);
		

		/*
		 * The user 
		 */
		Context initContext = null;
		Context envContext = null;
		// use LDAP password even for bibsonomy username 
		Boolean useLDAP = false;
		
		try {
			initContext = new InitialContext();
			envContext = (Context) initContext.lookup("java:/comp/env");
		} catch (NamingException ex) {
			log.error("Error when trying create initContext lookup for java:/comp/env via JNDI.", ex);
		}
		try {
			useLDAP = (Boolean) envContext.lookup("useLdapPasswordforBibsonomyLogin");
		} catch (NamingException ex) {
			log.error("Error when trying to read environment variable 'useLdapPasswordforBibsonomyLogin' via JNDI.", ex);
			useLDAP = false;
		}

		User user = null;
		
		log.info("useLDAP: " + useLDAP + "  - you can switch this by editing jndi properties in context.xml: java:/comp/env/useLdapPasswordforBibsonomyLogin");
		log.info("login method: " + command.getLoginMethod());

		if (useLDAP && username != null && hashedPassword != null  ) { 
			
			/*
			 * authentication via username and password via LDAP 
			 */
			
			// TODO: this is only for auth with bibsonomy's user name. ldap user id auth should be possible, too.
			String userId = null;
			String bibsonomyUsername = null;
			if ("ldap".equals(command.getLoginMethod()))
			{
				log.info("get username by ldap id ("+ username +")");
				bibsonomyUsername = adminLogic.getUsernameByLdapUserId(username);
				log.info("bibsonomyusername is "+bibsonomyUsername);
				
				// semi-auto-register, if username does not exist
				// check if user credentials are correct for ldap-login
				// if so, go to ldap registration step 2 (fill out user details form)
				userId = username;
			} 
			else
			{
				bibsonomyUsername = username;
			}
			
			if (null != bibsonomyUsername) 
			{
				// get user's ldap-id from database
				userId = adminLogic.getUserDetails(bibsonomyUsername).getLdapId();
			}
			LdapUserinfo ldapUserinfo = null;
			
			if (null != userId)
			{
				Ldap ldap = new Ldap();
	
				log.info("Trying to login user " + bibsonomyUsername + " via LDAP (uid="+userId+")");
				//log.info("password: "+password);
		        ldapUserinfo = ldap.checkauth(userId, password);
			}
			
			// if user has an ldap user id and password is wrong
			if ((null != userId) && (null == ldapUserinfo))
			{
				/*
				 * user credentials do not match --> show error message
				 */
				log.info("Login of user " + bibsonomyUsername + " failed.");
				errors.reject("error.login.failed");
				/*
				 * count failures
				 */
				grube.add(bibsonomyUsername);
				grube.add(inetAddress);
			}
			else if ((null == userId) && (null == ldapUserinfo)) {
				// user does has no ldap id in puma and can not authenticate at ldap
				// set useLDAP to false to make it possible to authenticate against bibonomy user table and treat user as normal internal user 
				useLDAP = false;
			}
			else
			{
				// ldap credentials are correct
				// if bibsonomyUsername is null, user does not exist in bibsonomy database
				// register user first -> redirect to ldap registration step 2 (fill out user details form)
				
				log.info("Login of user " + bibsonomyUsername + " accepted.");
				if (null == bibsonomyUsername) {
					// redirect
					log.info("Redirecting user to registration page");
					UserLDAPRegistrationCommand ldapCommand = new UserLDAPRegistrationCommand(); 
					
					// store username and password in session
					requestLogic.setSessionAttribute(InitUserFilter.REQ_ATTRIB_LOGIN_USER, userId);
					requestLogic.setSessionAttribute(InitUserFilter.REQ_ATTRIB_LOGIN_USER_PASSWORD, password);
					
					
					return new ExtendedRedirectView("/registerLDAP"
														+ "?step=2"
														+ "&registerUser.name=x"
														+ "&registerUser.password=x"
													);

//					return Views.REGISTER_USER_LDAP_FORM;
				}
				
				user = adminLogic.getUserDetails(bibsonomyUsername);
				
				/*
				 * add authentication cookie to response
				 */
				cookieLogic.addUserCookie(bibsonomyUsername, ldapUserinfo.getPasswordHashMd5Hex());

				/*
				 * update lastAccessTimestamp
				 */
				adminLogic.updateUser(user, UserUpdateOperation.UPDATE_LDAP_TIMESTAMP);
				
			}
			
		}

		
		if (!useLDAP && !present(username) && !present(hashedPassword)) {
			/*
			 * authentication via username and password 
			 */
		
			/*
			 * checking password of user
			 */
			user = adminLogic.getUserDetails(username);
			if (!hashedPassword.equals(user.getPassword())) {
				/*
				 * passwords do not match -> check password from reminder
				 */
				if (!password.equals(user.getReminderPassword())) {
					/*
					 * password does neither match real or reminder password --> show error message
					 */
					log.info("Login of user " + username + " failed.");
					errors.reject("error.login.failed");
					/*
					 * count failures
					 */
					grube.add(username);
					grube.add(inetAddress);
				} else {
					/*
					 * passwords do match -> check if reminder still valid or already expired.
					 */
					final Calendar now = Calendar.getInstance();
					/*
					 * set expiration date by adding the max number of minutes a password
					 * reminder is valid to the time where the user requested the reminder
					 */
					final Calendar reminderExpirationDate = Calendar.getInstance();
					reminderExpirationDate.setTime(user.getReminderPasswordRequestDate());
					reminderExpirationDate.add(Calendar.MINUTE, maxMinutesPasswordReminderValid);

					if (now.after(reminderExpirationDate)) {
						/*
						 * reminder expired
						 */
						log.info("Login of user " + username + " failed, because of expired password reminder.");
						errors.reject("error.login.reminderExpired");
					} else {
						/*
						 * reminder password correct (and used!) and reminder not expired. Send 
						 * user to password change page.
						 * 
						 * FIXME: migration of password_change neccessary! Using the session is
						 * not the best idea - find better solutions! Maybe a separate password-
						 * change page is the best, where the user enters his temporary password,
						 * and his new password (twice).
						 */
						requestLogic.setSessionAttribute("tmpUser", username); // FIXME: remove this!
						return new ExtendedRedirectView("/change_password");
					}
				}
			}		
			/*
			 * add authentication cookie to response
			 */
			cookieLogic.addUserCookie(username, hashedPassword);

		} else if (present(openID)) {
			/*
			 * OpenID authentication
			 */
			return handleOpenID(referer, openID);						
		}


		/*
		 * on error, send user back
		 */
		if (errors.hasErrors()) {
			command.setLoginMethod(requestLogic.getParameter("loginMethod"));
			return Views.LOGIN;
		}

		/*
		 * user successfully authenticated!
		 */

		/*
		 * flag spammers with a cookie 
		 * FIXME: here we sometimes get an NPE (why?)
		 */
		cookieLogic.addSpammerCookie(user.isSpammer());

		/*
		 * To prevent Session-Fixation attacks (see http://www.jsptutorial.org/content/session) 
		 * we invalidate the old session.
		 */
		requestLogic.invalidateSession();

		/*
		 * Redirect to the page the user is coming from. 
		 */
		return new ExtendedRedirectView(referer);
	}


	/**
	 * Handles OpenID authentication
	 * 
	 * @param referer
	 * @param openID
	 * @return
	 */
	private View handleOpenID(final String referer, final String openID) {
		String returnToUrl = null;
		try {
			returnToUrl = projectHome + "login?referer=" + URLEncoder.encode(referer, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			log.warn("Could not encode URL for login page.", ex);
		}

		/*
		 * redirect to OpenID provider
		 */
		try {
			return new ExtendedRedirectView(openIDLogic.authOpenIdRequest(requestLogic, openID, projectHome, returnToUrl, false));
		} catch (OpenIDException ex) {
			errors.reject("error.invalid_openid");
			return Views.ERROR;
		}
	}


	/**
	 * Sets the referer in the command using the referer from
	 * the requests referer header. Contains also some logic 
	 * to handle non-existing referer-headers. 
	 * 
	 * @param command
	 */
	private void setReferer(UserLoginCommand command) {
		if (!present(command.getReferer())) {
			/*
			 * no referer set in command
			 */
			final String referer = requestLogic.getReferer();
			/*
			 * check referer from header
			 */
			if (present(referer)) {
				/*
				 * there is a referer -> set it in command
				 */
				command.setReferer(referer);
				log.debug("Set referer to " + command.getReferer());
			} else {
				/*
				 * There is no referer. This probably means, that /login has been
				 * called the first time directly. After filling out the form and 
				 * sending it to the controller, the referer header would contain
				 * /login and this would be written into the referer attribute of
				 * the command (which would still be empty). Hence, the user will
				 * be redirected to /login after login ... which doesn't make any
				 * sense. To circumvent this, we set here the redirect page to /. 
				 */
				command.setReferer("/");
			}
		}
	}

	/**
	 * Check, if the user (or IP) has to wait until a login try is possible. 
	 * 
	 * @param username
	 * @param inetAddress
	 * @throws ServiceUnavailableException When the user (or IP) has to wait until another login try is possible.
	 */
	private void handleWaiting(final String username, final String inetAddress) throws ServiceUnavailableException {
		/*
		 * get the number of seconds the user has to wait
		 */
		final long remainingWaitSecondsIP   = grube.getRemainingWaitSeconds(inetAddress);
		final long remainingWaitSecondsName = grube.getRemainingWaitSeconds(username);
		/*
		 * take the maximum
		 */
		final long waitingSeconds = (remainingWaitSecondsIP > remainingWaitSecondsName ? remainingWaitSecondsIP : remainingWaitSecondsName);
		/*
		 * check in how many seconds the user is allowed to use this service 
		 */
		if (waitingSeconds > 5) {
			/*
			 * either ip or user name is blocked for more than 5 seconds from now --> log and send error page 
			 */
			log.warn("user " + username + " from IP " + inetAddress + " tried to login but still has to wait for max(" 
					+ remainingWaitSecondsName + ", " + remainingWaitSecondsIP + ") = " + waitingSeconds + " seconds.");

			/*
			 * Send user error message.
			 */
			throw new ServiceUnavailableException("error.service_unavailable", waitingSeconds);
		}
	}

	/**
	 * Handles a OpenID authentication request from provider
	 * 
	 * @param openID the OpenID url
	 * @return <code>true</code> if authentication succeeded else <code>false</code>
	 */
	private boolean handleOpenIDLogin(final String openID) {
		/*
		 * Response from OpenId Provider 
		 */	

		/*
		 * get instance of openid logic from session
		 */
		openIDLogic = (OpenID) requestLogic.getSessionAttribute(OpenID.OPENID_LOGIC_SESSION_ATTRIBUTE);

		/*
		 * verify the openid request
		 */
		if (openIDLogic != null) {
			
			
			final User user = openIDLogic.verifyResponse(requestLogic, false);

			if (user != null) {
				/*
				 * OpenID auth succeeded  
				 */

				/*
				 *  get username corresponding to openid 
				 */
				String username = adminLogic.getOpenIDUser(UserUtils.normalizeURL(openID));

				/*
				 *  user known -> login
				 */
				if (username != null) {
					final User registeredUser = adminLogic.getUserDetails(username);
					cookieLogic.addOpenIDCookie(registeredUser.getName(), openID, registeredUser.getPassword());	
					openIDLogic.extendOpenIDSession(requestLogic.getSession(), openID);
					return true;
				} 

				log.debug("OpenID user not found");
			}
		} else {
			/*
			 * FIXME: sensible error handling
			 */
		}
		return false;
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(final Errors errors) {
		/*
		 * here: check for binding errors
		 */
		this.errors = errors;
	}

	/** Returns, if validation is required for the given command. On default,
	 * for all incoming data validation is required.
	 * 
	 * @see org.bibsonomy.webapp.util.ValidationAwareController#isValidationRequired(java.lang.Object)
	 */
	public boolean isValidationRequired(final UserLoginCommand command) {
		/*
		 * TODO: When is validation really required?
		 */
		return true;
	}

	public Validator<UserLoginCommand> getValidator() {
		return new UserLoginValidator();
	}

	/**
	 * @see org.bibsonomy.webapp.util.RequestAware#setRequestLogic(org.bibsonomy.webapp.util.RequestLogic)
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * Set an instance of the {@link TeerGrube}. This is used to store waiting times for 
	 * IPs/users which failed too often to enter the correct username.
	 * 
	 * @param grube
	 */
	@Required
	public void setGrube(TeerGrube grube) {
		this.grube = grube;
	}

	/** Set the admin logic required by this controller to check the credentials of the user.
	 * @param adminLogic
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
	 * The maximal number of minutes, a password reminder is valid.
	 * 
	 * @param maxMinutesPasswordReminderValid
	 */
	public void setMaxMinutesPasswordReminderValid(int maxMinutesPasswordReminderValid) {
		this.maxMinutesPasswordReminderValid = maxMinutesPasswordReminderValid;
	}


	/**
	 * @param cookieLogic
	 */
	public void setCookieLogic(CookieLogic cookieLogic) {
		this.cookieLogic = cookieLogic;
	}


	/**
	 * @param openIDLogic
	 */
	public void setOpenIDLogic(OpenID openIDLogic) {
		this.openIDLogic = openIDLogic;
	}

	/** 
	 * The base URL of the project.
	 * 
	 * @param projectHome
	 */
	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}

}
