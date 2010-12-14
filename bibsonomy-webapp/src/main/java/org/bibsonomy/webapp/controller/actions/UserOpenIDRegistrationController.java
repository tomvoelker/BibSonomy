package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.HashUtils;
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
import org.bibsonomy.webapp.util.spring.security.rememberMeServices.CookieBasedRememberMeServices;
import org.bibsonomy.webapp.validation.UserOpenIDRegistrationValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * This controller handles the registration of users via OpenID
 * (see http://openid.net/)
 * 
 * @author Stefan Stützer
 * @author rja
 * @version $Id$
 */
public class UserOpenIDRegistrationController implements ErrorAware, ValidationAwareController<UserOpenIDLdapRegistrationCommand>, RequestAware, CookieAware{
	private static final Log log = LogFactory.getLog(UserOpenIDRegistrationController.class);
	
	protected LogicInterface adminLogic;
	private Errors errors = null;
	private RequestLogic requestLogic;
	private CookieLogic cookieLogic;
	private CookieBasedRememberMeServices openIdRememberMeServices;
	private AuthenticationManager authenticationManager;
	
	/**
	 * After successful registration, the user is redirected to this page. 
	 */
	private String successRedirect = "";

	/**
	 * Only users which were successfully authenticated using OpenID and
	 * whose OpenID does not exist in our database are allowed to use this
	 * controller.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(UserOpenIDLdapRegistrationCommand command) {
		log.debug("workOn() called");

		command.setPageTitle("OpenID registration");
		
		/* 
		 * If user is properly logged in: show error message
		 */
		if (command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedException("error.method_not_allowed");
		}

		/*
		 * If the user has been successfully authenticated using OpenID and he
		 * is not yet registered, his OpenID data is contained in the session.  
		 */
		final Object o = requestLogic.getSessionAttribute(FailureHandler.USER_TO_BE_REGISTERED);
		if (!present(o) || ! (o instanceof User)) {
			/*
			 * user must first login.
			 */
			return new ExtendedRedirectView("/login"+ 
					"?notice=" + "register.openid.step1" + 
					"&referer=" + UrlUtils.safeURIEncode(requestLogic.getCompleteRequestURL()));
		}
		
		
		/*
		 * user found in session - proceed with the registration 
		 */
		log.debug("got user from session");
		final User user = (User) o;
		
		
		/* 
		 * 2 = user has not been on form, yet -> fill it with OpenID data
		 * 3 = user has seen the form and possibly changed data
		 */
		if (command.getStep() == 2) {
			log.debug("step 2: start OpenID registration");
			/*
			 * fill command with data from OpenID
			 */
			command.setRegisterUser(user);
			/*
			 * ensure that we proceed to the next step
			 */
			command.setStep(3);
			/*
			 * return to form
			 */
			return Views.REGISTER_USER_OPENID_PROVIDER_FORM;
		}
		

		log.debug("step 3: complete OpenID registration");
		/* 
		 * if there are any errors in the form, we return back to fix them.
		 */
		if (errors.hasErrors()) {
			log.info("an error occoured: " + errors.toString());
			return Views.REGISTER_USER_OPENID_PROVIDER_FORM;
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
			 * we add the OpenID since it is shown to the user in the form
			 */
			registerUser.setOpenID(user.getOpenID());
			return Views.REGISTER_USER_OPENID_PROVIDER_FORM;
		}
		
		log.info("validation passed with " + errors.getErrorCount() + " errors, proceeding to access database");
		/*
		 * set the users inet address
		 */
		registerUser.setIPAddress(requestLogic.getInetAddress());
		/*
		 * before we store the user, we must ensure that he contains a proper 
		 * password and the OpenID
		 */
		registerUser.setPassword(generateRandomPassword());
		registerUser.setOpenID(user.getOpenID());
		/*
		 * create user in DB
		 */
		adminLogic.createUser(registerUser);
		/*
		 * delete user from session.
		 */
		requestLogic.setSessionAttribute(FailureHandler.USER_TO_BE_REGISTERED, null);

		/*
		 * log user into system
		 * 
		 */
		final UserDetails userDetails = new UserAdapter(registerUser);
		final Authentication authentication = new OpenIDAuthenticationToken(userDetails, userDetails.getAuthorities(), registerUser.getOpenID(), null);

		final Authentication authenticated = authenticationManager.authenticate(authentication);
		SecurityContextHolder.getContext().setAuthentication(authenticated);
		cookieLogic.createRememberMeCookie(openIdRememberMeServices, authenticated);

		/*
		 * present the success view
		 */
		return new ExtendedRedirectView(successRedirect);
	}

	private String generateRandomPassword() {
		final byte[] bytes = new byte[16];
		new Random().nextBytes(bytes);
		final String randomPassword = HashUtils.getMD5Hash(bytes);
		return randomPassword;
	}


	@Override
	public UserOpenIDLdapRegistrationCommand instantiateCommand() {
		final UserOpenIDLdapRegistrationCommand userOpenIDRegistrationCommand = new UserOpenIDLdapRegistrationCommand();
		/*
		 * add user to command
		 */
		userOpenIDRegistrationCommand.setRegisterUser(new User());
		return userOpenIDRegistrationCommand;		
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
		return new UserOpenIDRegistrationValidator();
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
	 * @param adminLogic - an instance of the logic interface with admin access.
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
	 * @param successRedirect
	 */
	public void setSuccessRedirect(String successRedirect) {
		this.successRedirect = successRedirect;
	}

	/**
	 * @return The OpenID remember me service of this controller.
	 */
	public CookieBasedRememberMeServices getOpenIdRememberMeServices() {
		return this.openIdRememberMeServices;
	}

	/**
	 * @param openIdRememberMeServices
	 */
	public void setOpenIdRememberMeServices(CookieBasedRememberMeServices openIdRememberMeServices) {
		this.openIdRememberMeServices = openIdRememberMeServices;
	}
	
	/**
	 * Sets the authentication manager used to authenticate the user after 
	 * successful registration.
	 * 
	 * @param authenticationManager
	 */
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
}