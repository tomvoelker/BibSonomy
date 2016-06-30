/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.bibsonomy.webapp.util.RequestAware;
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
 */
public class VuFindUserInitController implements MinimalisticController<OAuthCommand>, RequestAware {
	private static final Log log = LogFactory.getLog(VuFindUserInitController.class);
	
	public static final String UNKNOWN = "<unknown>";


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
	public View workOn(final OAuthCommand command) {
		this.getSamlAuthTool().ensureFreshAuthentication();
		Authentication authToken = null;
		if (AuthenticationUtils.getUserOrNull() == null) {
			// we have made sure that an authentication attempt was done but still got no user
			// -> try to create a new user from shibboleth credentials
			authToken = this.createNewLimitedUserAndAuthTokenForHer();
		}
		// if we are here, we have authenticated a user (maybe not yet logged in)
		// - otherwise exceptions would have been thrown
		try {
			if (authToken != null) {
				final Authentication auth = this.getAuthenticationManager().authenticate(authToken);
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
			command.setAuthorizeAction(AuthorizeAction.Authorize.toString());
			return this.oaAuthorizeController.workOn(command);
		} finally {
			this.requestLogic.invalidateSession();
			SecurityContextHolder.getContext().setAuthentication(null);
		}
	}

	protected Authentication createNewLimitedUserAndAuthTokenForHer() {
		Authentication authToken;
		final SAMLCredential samlCreds = getSamlCreds();
		final SamlRemoteUserId remoteUserId = this.getRemoteUserId(samlCreds);
		if (remoteUserId == null) {
			final String msg = "no userid after saml-login in " + this.getClass().getSimpleName();
			log.warn(msg);
			throw new AccessDeniedException(msg);
		}
		final User user = new User();
		/*
		 * Newly created limited users must have private settings to ensure their invisibility in the system.
		 */
		user.setSettings(new UserSettings());
		user.getSettings().setProfilePrivlevel(ProfilePrivlevel.PRIVATE);
		this.attributeExtractor.populate(user, samlCreds);
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
		user.setIPAddress(this.getRequestLogic().getInetAddress());
		user.setName(remoteUserId.getUserId());
		//user.setRole(Role.LIMITED);
		user.setRole(Role.DEFAULT);
		user.setToClassify(0);

		this.adminLogic.createUser(user);
		authToken = new SamlCredAuthToken(samlCreds);
		return authToken;
	}

	private SamlRemoteUserId getRemoteUserId(final SAMLCredential samlCreds) {
		if (samlCreds == null) {
			return null;
		}
		return this.attributeExtractor.getRemoteUserId(samlCreds);
	}

	/**
	 * @return {@link SAMLCredential} object from the spring threadlocal, null if nonexistent
	 */
	public static SAMLCredential getSamlCreds() {
		final Authentication auth = getAuth();
		if (auth == null) {
			return null;
		}
		final Object creds = auth.getCredentials();
		if (creds instanceof SAMLCredential) {
			return (SAMLCredential) creds;
		}
		return null;
	}

	/**
	 * @return {@link Authentication} object from the spring threadlocal, null if nonexistent
	 */
	public static Authentication getAuth() {
		final SecurityContext ctx = SecurityContextHolder.getContext();
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
	public void setOaAuthorizeController(final OAuthAuthorizeTokenController oaReqTokenController) {
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
	public void setAttributeExtractor(final SamlUserAttributeMapping attributeExtractor) {
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
	public void setAdminLogic(final LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}
	
	protected AuthenticationManager getAuthenticationManager() {
		return this.authenticationManager;
	}

	/**
	 * @param authenticationManager
	 */
	public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	/**
	 * @return the loginTool
	 */
	public SamlAuthenticationTool getSamlAuthTool() {
		if (this.samlAuthTool == null) {
			this.samlAuthTool = new SamlAuthenticationTool(this.getRequestLogic(), null);
		}
		return this.samlAuthTool;
	}
	
	/**
	 * @param requestLogic
	 */
	@Override
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * @return the requestLogic
	 */
	public RequestLogic getRequestLogic() {
		return this.requestLogic;
	}
}
