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
package org.bibsonomy.webapp.util.spring.security.saml;

import java.util.Collection;

import org.bibsonomy.common.enums.AuthMethod;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.spring.security.exceptions.SpecialAuthMethodRequiredException;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author jensi
 */
public class SamlAuthenticationTool {
	private RequestLogic requestLogic;
	private Collection<String> allowedParams;
	
	private static final String REL_STATE_CHECK_SESSION_ATTR = SamlAuthenticationTool.class.getName() + ".session.checkattr";

	/**
	 * initializing constructor
	 * @param requestLogic requestLogic for which the new object is going to be used
	 * @param allowedParams allowedParams parameters from the current request that are allowed to be re-added in a GET-Redirect after authentication
	 */
	public SamlAuthenticationTool(RequestLogic requestLogic, Collection<String> allowedParams) {
		this.requestLogic = requestLogic;
		this.allowedParams = allowedParams;
	}
	
	/**
	 * default bean constructor
	 */
	public SamlAuthenticationTool() {
	}

	/**
	 * @param requestLogic
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}
	
	/**
	 * ensures that an authentication procedure has been triggered.
	 * 
	 * @throws SpecialAuthMethodRequiredException
	 *             if a new authentication step is required (this informs
	 *             ExceptionTranslationFilter to trigger the authentication redirect)
	 */
	public void ensureFreshAuthentication() {
		if (isFreshlyAuthenticated() == false) {
			throw new SpecialAuthMethodRequiredException(AuthMethod.SAML);
		}
	}
	
	/**
	 * @return true if an authentication procedure has just finished, otherwise false
	 */
	public boolean isFreshlyAuthenticated() {
		if (isLoginDone() == false) {
			requestLogic.invalidateSession();
			SecurityContextHolder.getContext().setAuthentication(null);
			setRelayState();
			return false;
		}
		clearRelayState();
		return true;
	}
	
	/**
	 * clears the relaystate
	 */
	public void clearRelayState() {
		requestLogic.setSessionAttribute(REL_STATE_CHECK_SESSION_ATTR, null);
	}
	
	/**
	 * Sets the relaystate for the next saml request to the current url
	 */
	public void setRelayState() {
		String relayStateToken = UserUtils.generateRandomPassword();
		requestLogic.setNextRelayState(requestLogic.getUrlBuilder().clearParamsRetaining(allowedParams).addParameter("RelayState", relayStateToken).asString());
		requestLogic.setSessionAttribute(REL_STATE_CHECK_SESSION_ATTR, relayStateToken);
	}

	protected boolean isLoginDone() {
		String relState = requestLogic.getRelayState();
		if (ValidationUtils.present(relState) == false) {
			return false;
		}
		return relState.equals(getRelayState());
	}

	/**
	 * @return the relaystate
	 */
	public String getRelayState() {
		return (String) requestLogic.getSessionAttribute(REL_STATE_CHECK_SESSION_ATTR);
	}
}
