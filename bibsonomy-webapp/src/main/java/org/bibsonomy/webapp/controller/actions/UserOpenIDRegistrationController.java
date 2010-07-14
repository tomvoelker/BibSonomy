package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.actions.UserOpenIDRegistrationCommand;
import org.bibsonomy.webapp.util.CookieAware;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.auth.OpenID;
import org.bibsonomy.webapp.validation.UserOpenIDRegistrationValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.openid4java.OpenIDException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * This controller handles the registration of users via OpenID
 * (see http://openid.net/)
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class UserOpenIDRegistrationController implements ErrorAware, ValidationAwareController<UserOpenIDRegistrationCommand>, RequestAware, CookieAware{
	private static final Log log = LogFactory.getLog(UserOpenIDRegistrationController.class);
	
	protected LogicInterface logic;
	protected LogicInterface adminLogic;
	private Errors errors = null;
	private RequestLogic requestLogic;
	private CookieLogic cookieLogic;
	private OpenID openIDLogic;
	
	private String projectHome;
	
	/**
	 * After successful registration, the user is redirected to this page. 
	 */
	private String successRedirect = "";

	@Override
	public View workOn(UserOpenIDRegistrationCommand command) {
		log.debug("workOn() called");

		command.setPageTitle("OpenID registration");
		
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();
		
		/* Check user role
		 * 
		 * If user is logged in and not an admin: show error message
		 */
		if (context.isUserLoggedIn() && !Role.ADMIN.equals(loginUser.getRole())) {
			log.warn("User " + loginUser.getName() + " tried to access user registration without having role " + Role.ADMIN);
			throw new AccessDeniedException("error.method_not_allowed");
		}

		if (errors.hasErrors()) {
			/*
			 * error in step 4 -> redirect to open id registration form
			 */
			if (command.getStep() == 4)
				return Views.REGISTER_USER_OPENID_PROVIDER_FORM;
			
			return Views.REGISTER_USER_OPENID;
		}
		
		/*
		 * Registration steps 
		 */
		if (command.getStep() == 1) {			
			
			log.debug("step 1: fill form");
			/*
			 * show form to enter OpenID
			 */			
			return Views.REGISTER_USER_OPENID;
		} else if (command.getStep() == 2) {
			
			log.debug("step 2: redirect to provider ");
			/*
			 *  if OpenID is present -> redirect to OpenID provider
			 */
			try {
				String redirect = openIDLogic.authOpenIdRequest(requestLogic, command.getRegisterUser().getOpenID(), projectHome, 
						projectHome + "registerOpenID?step=3", true);
				return new ExtendedRedirectView(redirect);
			} catch (OpenIDException ex) {
				errors.reject("error.invalid_openid");
				return Views.ERROR;
			}			
		} else if (command.getStep() == 3) {
		
			log.debug("step 3: show form");
			
			/*
			 * get instance of openid logic from session
			 */
			openIDLogic = (OpenID) requestLogic.getSessionAttribute(OpenID.OPENID_LOGIC_SESSION_ATTRIBUTE);
			
			/*
			 * show profile form prefilled with information from the openID provider
			 */
			User openIDUser = openIDLogic.verifyResponse(requestLogic, true);
			
			/*
			 * user succesfully authenticated by OpenID provider 
			 */
			if (openIDUser != null) {
				command.getRegisterUser().setName(openIDUser.getName());
				command.getRegisterUser().setEmail(openIDUser.getEmail());
				command.getRegisterUser().setRealname(openIDUser.getRealname());
				command.getRegisterUser().setGender(openIDUser.getGender());
				command.getRegisterUser().setPlace(openIDUser.getPlace());
				command.getRegisterUser().setOpenID(openIDUser.getOpenID());
			}			
			return Views.REGISTER_USER_OPENID_PROVIDER_FORM;
		} else if (command.getStep() == 4) {
			
			log.debug("step 4: complete OpenID registration");
			/*
			 * complete registration process and save user to database
			 */
						
			/* Check cookies
			 * 
			 * Check, if user has cookies enabled (there should be at least a "JSESSIONID" cookie)
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
			 * check if a user already registered this OpenID 
			 */
			if (registerUser.getOpenID() != null && logic.getOpenIDUser(registerUser.getOpenID()) != null) {
				/*
				 * OpenID already registered 
				 */
				errors.rejectValue("registerUser.openID", "error.field.duplicate.user.openid");
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
			 * if the user is not logged in, we need an instance of the logic interface
			 * with admin access 
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
			 * FIXME: choose better random pw
			 * This is a severe security hole! We need to update ALL passwords 
			 * of open ID users in the database when this is fixed. Otherwise,
			 * users won't be able to log in.
			 * 
			 */
			final String password = StringUtils.getMD5Hash(registerUser.getName() + "OPENID_");
			registerUser.setPassword(password);
						
			/*
			 * create user in DB
			 */
			logic.createUser(registerUser);		
			
			/*
			 * log user into system
			 */
			cookieLogic.addOpenIDCookie(registerUser.getName(), command.getRegisterUser().getOpenID(), registerUser.getPassword());
			openIDLogic.extendOpenIDSession(requestLogic.getSession(),  command.getRegisterUser().getOpenID());
			
			/*
			 * present the success view
			 */
			return new ExtendedRedirectView(successRedirect);
		}
		
		return Views.REGISTER_USER_OPENID;
	}

	@Override
	public UserOpenIDRegistrationCommand instantiateCommand() {
		final UserOpenIDRegistrationCommand userOpenIDRegistrationCommand = new UserOpenIDRegistrationCommand();
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
	public Validator<UserOpenIDRegistrationCommand> getValidator() {
		return new UserOpenIDRegistrationValidator();
	}
	
	@Override
	public boolean isValidationRequired(UserOpenIDRegistrationCommand command) {
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
	 * @param logic logic interface
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
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
	 *  @param openIDLogic - an instance of the OpenID logic
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

	/** 
	 * After successful registration, the user is redirected to this page.
	 * @param successRedirect
	 */
	public void setSuccessRedirect(String successRedirect) {
		this.successRedirect = successRedirect;
	}		
}