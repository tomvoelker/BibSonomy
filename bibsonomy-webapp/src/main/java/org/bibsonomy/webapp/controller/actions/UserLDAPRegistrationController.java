package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.actions.UserOpenIDLdapRegistrationCommand;
import org.bibsonomy.webapp.util.CookieAware;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.UserAdapter;
import org.bibsonomy.webapp.util.spring.security.handler.FailureHandler;
import org.bibsonomy.webapp.util.spring.security.rememberMeServices.LDAPRememberMeServices;
import org.bibsonomy.webapp.validation.UserLDAPRegistrationValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * This controller handles the registration of users via LDAP
 * 
 * @author Sven Stefani
 * @author rja
 * @version $Id$
 */
/**
 * @author rja
 *
 */
public class UserLDAPRegistrationController implements ErrorAware, ValidationAwareController<UserOpenIDLdapRegistrationCommand>, RequestAware, CookieAware {
	private static final Log log = LogFactory.getLog(UserLDAPRegistrationController.class);


	protected LogicInterface adminLogic;
	private Errors errors = null;
	private RequestLogic requestLogic;
	private CookieLogic cookieLogic;
	private LDAPRememberMeServices ldapRememberMeServices;

	/**
	 * After successful registration, the user is redirected to this page.
	 */
	private String successRedirect = "";

	/**
	 * Only users which were successfully authenticated using LDAP and whose 
	 * LDAP ID does not exist in our database are allowed to use this 
	 * controller.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(UserOpenIDLdapRegistrationCommand command) {
		log.debug("workOn() called");

		command.setPageTitle("LDAP registration");

		/*
		 * If the user is properly logged in: show error message
		 */
		if (command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedException("error.method_not_allowed");
		}


		/*
		 * If the user has been successfully authenticated using LDAP and he
		 * is not yet registered, his LDAP data is contained in the session.  
		 */
		final Object o = requestLogic.getSessionAttribute(FailureHandler.USER_TO_BE_REGISTERED);
		if (!present(o) || ! (o instanceof User)) {
			/*
			 * user must first login.
			 */
			return new ExtendedRedirectView("/login"+ 
					"?notice=" + "register.ldap.step1" + 
					"&referer=" + UrlUtils.safeURIEncode(requestLogic.getCompleteRequestURL()));
		}
		

		/*
		 * user found in session - proceed with the registration 
		 */
		log.debug("got user from session");
		final User user = (User) o;

		/*
		 * 2 = user has not been on form, yet -> fill it with LDAP data
		 * 3 = user has seen the form and possibly changed data
		 */
		if (command.getStep() == 2) {
			log.debug("step 3: start LDAP registration");
			/*
			 * fill command with data from LDAP
			 */
			command.setRegisterUser(user);
			/*
			 * generate a new user name
			 */
			user.setName(generateUserName(user));
			/*
			 * ensure that we proceed to the next step
			 */
			command.setStep(3);
			/*
			 * return to form
			 */
			return Views.REGISTER_USER_LDAP_FORM;
		}
		
		
		
		log.debug("step 3: complete LDAP registration");
		/* 
		 * if there are any errors in the form, we return back to fix them.
		 */
		if (errors.hasErrors()) {
			log.info("an error occoured: " + errors.toString());
			return Views.REGISTER_USER_LDAP_FORM;
		}
		/*
		 * no errors: try to store user
		 */
		/*
		 * user that wants to register (form data)
		 */
		final User registerUser = command.getRegisterUser();
		/*
		 * check, if user name already exists
		 */
		if (present(registerUser.getName()) && present(adminLogic.getUserDetails(registerUser.getName()).getName())) {
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
			 * we add the LDAP id since it is shown to the user in the form
			 */
			registerUser.setLdapId(user.getLdapId());
			return Views.REGISTER_USER_LDAP_FORM;
		}
		log.info("validation passed with " + errors.getErrorCount() + " errors, proceeding to access database");
		/*
		 * set the users inet address
		 */
		registerUser.setIPAddress(requestLogic.getInetAddress());
		/*
		 * before we store the user, we must ensure that he contains the 
		 * password and the LDAP id
		 * 
		 * FIXME: What shall be the password in BibSonomy's database?
		 */
		registerUser.setPassword(StringUtils.getMD5Hash(user.getPassword()));
		registerUser.setLdapId(user.getLdapId());
		/*
		 * create user in DB
		 */
		adminLogic.createUser(registerUser);
		/*
		 * FIXME: delete user from session.
		 */

		/*
		 * log user into system
		 */
		final UserDetails userDetails = new UserAdapter(registerUser);
		final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword());

		cookieLogic.createRememberMeCookie(ldapRememberMeServices, authentication);

		/*
		 * present the success view
		 */
		return new ExtendedRedirectView(successRedirect);
	}

	private String generateUserName(final User user) {
		/*
		 * Find user name which does not exist yet in the database.
		 * 
		 * check if username is already used and try another
		 */
		String newName = user.getRealname().replaceAll(".*\\s+", "").toLowerCase();
		int tryCount = 0;
		log.info("try existence of username: "+newName);
		while ((newName.equalsIgnoreCase(adminLogic.getUserDetails(newName).getName())) && (tryCount < 101)) {
			try {
				if (tryCount == 0) {
					// try first character of forename concatenated with surename
					// bugs bunny => bbunny
					newName = user.getName().substring(0, 1).toLowerCase().concat(newName);
				} else if (tryCount == 100) {
					// now use first character of fore- and first two characters of surename concatenated with ldap user id 
					// bugs bunny => bbu01234567
					newName = newName.substring(0, 3).concat(user.getLdapId());
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
			} catch (IndexOutOfBoundsException ex) {
				/*
				 * if some substring values are out of range, catch exception and use surename
				 */
				newName = user.getRealname().replaceAll(".*\\s+", "").toLowerCase();
				tryCount = 99;
			}
		}
		return newName;
	}

	@Override
	public UserOpenIDLdapRegistrationCommand instantiateCommand() {
		final UserOpenIDLdapRegistrationCommand userLdapRegistrationCommand = new UserOpenIDLdapRegistrationCommand();
		/*
		 * add user to command
		 */
		userLdapRegistrationCommand.setRegisterUser(new User());
		return userLdapRegistrationCommand;
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
	public Validator<UserOpenIDLdapRegistrationCommand> getValidator() {
		return new UserLDAPRegistrationValidator();
	}

	@Override
	public boolean isValidationRequired(UserOpenIDLdapRegistrationCommand command) {
		return true;
	}

	@Override
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	@Override
	public void setCookieLogic(CookieLogic cookieLogic) {
		this.cookieLogic = cookieLogic;
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
	 * After successful registration, the user is redirected to this page.
	 * 
	 * @param successRedirect
	 */
	public void setSuccessRedirect(String successRedirect) {
		this.successRedirect = successRedirect;
	}

	/**
	 * @return The remember me service.
	 */
	public LDAPRememberMeServices getLdapRememberMeServices() {
		return this.ldapRememberMeServices;
	}

	/**
	 * @param ldapRememberMeServices
	 */
	public void setLdapRememberMeServices(LDAPRememberMeServices ldapRememberMeServices) {
		this.ldapRememberMeServices = ldapRememberMeServices;
	}
}