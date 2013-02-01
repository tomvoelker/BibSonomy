package org.bibsonomy.webapp.controller.actions;

import org.apache.log4j.Logger;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.spring.security.AuthenticationUtils;
import org.bibsonomy.webapp.command.VuFindUserInitCommand;
import org.bibsonomy.webapp.controller.opensocial.OAuthAuthorizeTokenController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.authentication.SamlCredAuthToken;
import org.bibsonomy.webapp.util.spring.security.saml.SamlAuthenticationTool;
import org.bibsonomy.webapp.util.spring.security.userattributemapping.SamlUserAttributeMapping;
import org.bibsonomy.webapp.validation.UserValidator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;


/**
 * This controller is called directly after the first vuFind login of a user.
 * It generates an oAuth-token request and ensures that there is a bibsonomy user for the remoteAuthentication (shibboleth) user. A new user is generated if required.
 * 
 * @author jensi
 *
 */
public class VuFindUserInitController implements MinimalisticController<VuFindUserInitCommand> {
	private static final Logger log = Logger.getLogger(VuFindUserInitController.class);

	private OAuthAuthorizeTokenController oaAuthorizeController;
	
	private SamlUserAttributeMapping attributeExtractor;
	
	private LogicInterface adminLogic;

	private AuthenticationManager authenticationManager;

	private RequestLogic requestLogic;

	private SamlAuthenticationTool samlAuthTool;

	
	@Override
	public VuFindUserInitCommand instantiateCommand() {
		return new VuFindUserInitCommand();
	}

	@Override
	public View workOn(VuFindUserInitCommand command) {
		getSamlAuthTool().ensureFreshAuthentication();
		Authentication authToken = null;
		if (AuthenticationUtils.getUserOrNull() == null) {
			// we have made sure that an authentication attempt was done but still got no user
			// -> try to create a new user from shibboleth credentials
			authToken = createNewUserAndAuthTokenForHer();
		}
		// if we are here, we have authenticated a user (maybe not yet logged in)
		// - otherwise exceptions would have been thrown
		try {
			if (authToken != null) {
				Authentication auth = getAuthenticationManager().authenticate(authToken);
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
			return oaAuthorizeController.workOn(command);
		} finally {
			requestLogic.invalidateSession();
			SecurityContextHolder.getContext().setAuthentication(null);
		}
	}

	protected Authentication createNewUserAndAuthTokenForHer() {
		Authentication authToken;
		SAMLCredential samlCreds = getSamlCreds();
		SamlRemoteUserId remoteUserId = getRemoteUserId(samlCreds);
		if (remoteUserId == null) {
			String msg = "no userid after saml-login in " + getClass().getSimpleName();
			log.warn(msg);
			throw new AccessDeniedException(msg);
		}
		User user = new User();
		attributeExtractor.populate(user, samlCreds);
		if (ValidationUtils.present(user.getRealname()) == false) {
			user.setRealname("<unknown>");
		}
		if (ValidationUtils.present(user.getPassword()) == false) {
			user.setPassword(UserUtils.generateRandomPassword());
		}
		if (ValidationUtils.present(user.getEmail()) == false) {
			user.setEmail("<unknown>");
		}
		user.setIPAddress(getRequestLogic().getInetAddress());
		user.setName(generateUserName(user, remoteUserId));

		this.adminLogic.createUser(user);
		authToken = new SamlCredAuthToken(samlCreds);
		return authToken;
	}

	private SamlRemoteUserId getRemoteUserId(SAMLCredential samlCreds) {
		if (samlCreds == null) {
			return null;
		}
		return attributeExtractor.getRemoteUserId(samlCreds);
	}

	/**
	 * @return {@link SAMLCredential} object from the spring threadlocal, null if nonexistent
	 */
	public static SAMLCredential getSamlCreds() {
		Authentication auth = getAuth();
		if (auth == null) {
			return null;
		}
		Object creds = auth.getCredentials();
		if (creds instanceof SAMLCredential) {
			return (SAMLCredential) creds;
		}
		return null;
	}

	/**
	 * @return {@link Authentication} object from the spring threadlocal, null if nonexistent
	 */
	public static Authentication getAuth() {
		SecurityContext ctx = SecurityContextHolder.getContext();
		if (ctx == null) {
			return null;
		}
		return ctx.getAuthentication();
	}
	
	private String generateUserName(final User user, SamlRemoteUserId sruid) {
		/*
		 * Find user name which does not exist yet in the database.
		 * 
		 * check if username is already used and try another
		 */
		String newName = cleanUserName(user.getRealname());
		int tryCount = 0;
		//log.debug("try existence of username: " + newName);
		while ((newName.equalsIgnoreCase(this.adminLogic.getUserDetails(newName).getName())) && (tryCount < 101)) {
			try {
				if (tryCount == 0) {
					// try first character of forename concatenated with surname
					// bugs bunny => bbunny
					newName = cleanUserName(user.getRealname()).substring(0, 1).concat(newName);
				} else if (tryCount == 100) {
					// now use first character of fore- and first two characters of surename concatenated with user id 
					// bugs bunny => bbu01234567
					String remoteUserId = sruid.getUserId();
					newName = cleanUserName(newName.substring(0, 3).concat(remoteUserId));
				} else {
					// try first character of forename concatenated with surename concatenated with current number
					// bugs bunny => bbunnyX where X is between 1 and 9
					if (tryCount==1) {
						// add trycount to newName
						newName = cleanUserName(newName.concat(Integer.toString(tryCount)));
					} else { 
						// replace last two characters of string with trycount
						newName = cleanUserName(newName.substring(0, newName.length() - Integer.toString(tryCount-1).length()).concat(Integer.toString(tryCount)));
					}
				}
				//log.debug("try existence of username: " + newName + " (" + tryCount + ")");
				tryCount++;
			} catch (final IndexOutOfBoundsException ex) {
				/*
				 * if some substring values are out of range, catch exception and use surename
				 */
				newName = cleanUserName(user.getRealname());
				tryCount = 99;
			}
		}
		return newName;
	}
	
	private static String cleanUserName(final String name) {
		if (!ValidationUtils.present(name)) {
			return "";
		}
		return UserValidator.USERNAME_DISALLOWED_CHARACTERS_PATTERN.matcher(name).replaceAll("").toLowerCase();
	}
	

	/**
	 * @return the regular oAuth controller
	 */
	public OAuthAuthorizeTokenController getOaAuthorizeController() {
		return this.oaAuthorizeController;
	}

	/**
	 * @param oaReqTokenController the regular oAuth controller
	 */
	public void setOaAuthorizeController(OAuthAuthorizeTokenController oaReqTokenController) {
		this.oaAuthorizeController = oaReqTokenController;
	}

	/**
	 * @return the attributeExtractor
	 */
	public SamlUserAttributeMapping getAttributeExtractor() {
		return this.attributeExtractor;
	}

	/**
	 * @param attributeExtractor the attributeExtractor to set
	 */
	public void setAttributeExtractor(SamlUserAttributeMapping attributeExtractor) {
		this.attributeExtractor = attributeExtractor;
	}

	/**
	 * @return the adminLogic
	 */
	public LogicInterface getAdminLogic() {
		return this.adminLogic;
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}
	
    protected AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    /**
     * @param authenticationManager
     */
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

	/**
	 * @return the loginTool
	 */
	public SamlAuthenticationTool getSamlAuthTool() {
		if (this.samlAuthTool == null) {
			this.samlAuthTool = new SamlAuthenticationTool(getRequestLogic());
		}
		return this.samlAuthTool;
	}
	
	/**
	 * @param requestLogic
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * @return the requestLogic
	 */
	public RequestLogic getRequestLogic() {
		return this.requestLogic;
	}
}
