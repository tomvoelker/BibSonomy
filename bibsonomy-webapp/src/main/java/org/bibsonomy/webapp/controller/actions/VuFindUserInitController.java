package org.bibsonomy.webapp.controller.actions;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.spring.security.AuthenticationUtils;
import org.bibsonomy.webapp.command.opensocial.OAuthCommand;
import org.bibsonomy.webapp.command.opensocial.OAuthCommand.AuthorizeAction;
import org.bibsonomy.webapp.controller.opensocial.OAuthAuthorizeTokenController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.authentication.SamlCredAuthToken;
import org.bibsonomy.webapp.util.spring.security.saml.SamlAuthenticationTool;
import org.bibsonomy.webapp.util.spring.security.userattributemapping.SamlUserAttributeMapping;
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
public class VuFindUserInitController implements MinimalisticController<OAuthCommand> {
	public static final String UNKNOWN = "<unknown>";

	private static final Logger log = Logger.getLogger(VuFindUserInitController.class);

	private OAuthAuthorizeTokenController oaAuthorizeController;
	
	private SamlUserAttributeMapping attributeExtractor;
	
	private LogicInterface adminLogic;

	private AuthenticationManager authenticationManager;

	private RequestLogic requestLogic;

	private SamlAuthenticationTool samlAuthTool;

	
	@Override
	public OAuthCommand instantiateCommand() {
		return new OAuthCommand();
	}

	@Override
	public View workOn(OAuthCommand command) {
		getSamlAuthTool().ensureFreshAuthentication();
		Authentication authToken = null;
		if (AuthenticationUtils.getUserOrNull() == null) {
			// we have made sure that an authentication attempt was done but still got no user
			// -> try to create a new user from shibboleth credentials
			authToken = createNewLimitedUserAndAuthTokenForHer();
		}
		// if we are here, we have authenticated a user (maybe not yet logged in)
		// - otherwise exceptions would have been thrown
		try {
			if (authToken != null) {
				Authentication auth = getAuthenticationManager().authenticate(authToken);
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
			command.setAuthorizeAction(AuthorizeAction.Authorize.toString());
			return oaAuthorizeController.workOn(command);
		} finally {
			requestLogic.invalidateSession();
			SecurityContextHolder.getContext().setAuthentication(null);
		}
	}

	protected Authentication createNewLimitedUserAndAuthTokenForHer() {
		Authentication authToken;
		SAMLCredential samlCreds = getSamlCreds();
		SamlRemoteUserId remoteUserId = getRemoteUserId(samlCreds);
		if (remoteUserId == null) {
			String msg = "no userid after saml-login in " + getClass().getSimpleName();
			log.warn(msg);
			throw new AccessDeniedException(msg);
		}
		User user = new User();
		/*
		 * Newly created limited users must have private settings to ensure their invisibility in the system.
		 */
		user.setSettings(new UserSettings());
		user.getSettings().setProfilePrivlevel(ProfilePrivlevel.PRIVATE);
		attributeExtractor.populate(user, samlCreds);
		// we assume that the real name has been set by the attributeExtractor
		if (ValidationUtils.present(user.getRealname()) == false) {
			user.setRealname("");
		}
		if (ValidationUtils.present(user.getPassword()) == false) {
			user.setPassword(UserUtils.generateRandomPassword());
		}
		if (ValidationUtils.present(user.getEmail()) == false) {
			user.setEmail(UNKNOWN);
		}
		user.setIPAddress(getRequestLogic().getInetAddress());
		user.setName(remoteUserId.getUserId());
		user.setRole(Role.LIMITED);

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
			this.samlAuthTool = new SamlAuthenticationTool(getRequestLogic(), null);
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
