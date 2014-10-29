package org.bibsonomy.rest;

import static org.bibsonomy.util.ValidationUtils.present;

import javax.servlet.http.HttpServletRequest;

import org.apache.shindig.auth.SecurityToken;
import org.bibsonomy.database.ShindigDBLogicUserInterfaceFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.opensocial.oauth.OAuthRequestValidator;
import org.bibsonomy.rest.exceptions.AuthenticationException;

/**
 * OAuth authentication
 *
 * @author dzo
 */
public class OAuthAuthenticationHandler implements AuthenticationHandler<SecurityToken> {

	/** handles OAuth requests */
	private OAuthRequestValidator oauthValidator;

	/** logic interface factory for handling oauth requests */
	private ShindigDBLogicUserInterfaceFactory oauthLogicFactory;
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.AuthenticationHandler#canAuthenticateUser(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public boolean canAuthenticateUser(SecurityToken securityToken) {
		return present(securityToken);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.AuthenticationHandler#extractAuthentication(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public SecurityToken extractAuthentication(HttpServletRequest request) {
		return this.oauthValidator.getSecurityTokenFromRequest(request);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.AuthenticationHandler#authenticateUser(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public LogicInterface authenticateUser(SecurityToken securityToken) throws AuthenticationException {
		if (securityToken.isAnonymous()) {
			throw new AuthenticationException(AuthenticationHandler.NO_AUTH_ERROR);
		}
		return this.oauthLogicFactory.getLogicAccess(securityToken);
	}

	/**
	 * @param oauthValidator the oauthValidator to set
	 */
	public void setOauthValidator(OAuthRequestValidator oauthValidator) {
		this.oauthValidator = oauthValidator;
	}

	/**
	 * @param oauthLogicFactory the oauthLogicFactory to set
	 */
	public void setOauthLogicFactory(ShindigDBLogicUserInterfaceFactory oauthLogicFactory) {
		this.oauthLogicFactory = oauthLogicFactory;
	}
}
