package org.bibsonomy.webapp.util.spring.security.saml.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.context.SAMLMessageContext;

/**
 * This class is necessary to feed a custom relaystate into the spring saml implementation code.
 *  The relaystate is a parameter that is required by by the SAML standard to be send back to the SP when the IdP receives it from the SP.
 *  It allows identification of responses to a particular authentication request.
 *  
 * @author jensi
 */
public class RelayStateSamlContextProviderImpl extends SAMLContextProviderImpl {
	/**
	 * name of the request attribute used to feed a custom relaystate into the spring saml implementation code
	 */
	public static final String SAML_RELAYSTATE_ATTR_NAME = RelayStateSamlContextProviderImpl.class.getName() + ".relaystate";

	@Override
	public SAMLMessageContext getLocalAndPeerEntity(HttpServletRequest request, HttpServletResponse response) throws MetadataProviderException {
		SAMLMessageContext ctx = super.getLocalAndPeerEntity(request, response);
		Object attr = request.getAttribute(SAML_RELAYSTATE_ATTR_NAME);
		if (attr instanceof String) {
			ctx.setRelayState((String)attr);
		}
		return ctx;
	}
}
