package org.bibsonomy.webapp.util.spring.security.saml;

import org.bibsonomy.common.enums.AuthMethod;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.spring.security.exceptions.SpecialAuthMethodRequiredException;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author jensi
 * @version $Id$
 */
public class SamlAuthenticationTool {
	private RequestLogic requestLogic;
	private static final String REL_STATE_CHECK_SESSION_ATTR = SamlAuthenticationTool.class.getName() + ".session.checkattr";
	

	/**
	 * @param requestLogic
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}
	
	
	/**
	 * ensures that an authentication procedure has been triggered.
	 */
	public void ensureFreshAuthentication() {
		if (isLoginDone() == false) {
			requestLogic.invalidateSession();
			SecurityContextHolder.getContext().setAuthentication(null);
			setRelayState();
			throw new SpecialAuthMethodRequiredException(AuthMethod.SAML);
		}
		clearLoginDoneState();
	}
	
	protected void clearLoginDoneState() {
		requestLogic.setSessionAttribute(REL_STATE_CHECK_SESSION_ATTR, null);
	}
	
	protected void setRelayState() {
		String relayStateToken = UserUtils.generateRandomPassword();
		requestLogic.setNextRelayState(requestLogic.getUrlBuilder().addParameter("RelayState", relayStateToken).asString());
		requestLogic.setSessionAttribute(REL_STATE_CHECK_SESSION_ATTR, relayStateToken);
	}

	protected boolean isLoginDone() {
		String relState = requestLogic.getRelayState();
		if (ValidationUtils.present(relState) == false) {
			return false;
		}
		return relState.equals(requestLogic.getSessionAttribute(REL_STATE_CHECK_SESSION_ATTR));
	}
}
