package org.bibsonomy.webapp.util.spring.security.rememberMeServices;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.bibsonomy.util.ValidationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.saml.SAMLCredential;

/**
 * Adapter to make the {@link #getCookieName()} of
 * {@link org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices}
 * public
 * 
 * Also checks if rememberMe services are requested inside the SAML relaystate
 * 
 * @author dzo
 * @version $Id: TokenBasedRememberMeServices.java,v 1.4 2012-09-17 12:51:00
 *          nosebrain Exp $
 */
public class TokenBasedRememberMeServices extends org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices implements CookieBasedRememberMeServices {
	private static final Log log = LogFactory.getLog(TokenBasedRememberMeServices.class);

	/**
	 * default constructor
	 * 
	 * @param key
	 * @param userDetailsService
	 */
	public TokenBasedRememberMeServices(final String key, final UserDetailsService userDetailsService) {
		super(key, userDetailsService);
	}

	@Override
	public String getCookieName() {
		return super.getCookieName();
	}

	@Override
	protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
		return (super.rememberMeRequested(request, parameter) || rememberMeRequestedInSamlRelayState());
	}

	/**
	 * SAML (Shibboleth) Single-Sign-On authenticates the user in a redirect coming back from the IdP. This redirect URL does not contain the rememberMe parameter anymore. Instead any state must be preserved inside a relayState parameter which is passed through the redirect process
	 * @return whether rememberme was requested for a SAML login
	 */
	private boolean rememberMeRequestedInSamlRelayState() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Object credentials = authentication.getCredentials();
			if (credentials instanceof SAMLCredential) {
				SAMLCredential samlCredential = (SAMLCredential) credentials;
				String relayState = samlCredential.getRelayState();
				URI uri;
				if (ValidationUtils.present(relayState)) {
					try {
						uri = new URI(relayState);
						List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
						for (NameValuePair nvp : params) {
							if (getParameter().equals(nvp.getName())) {
								return isPositiveRememberMeValue(nvp.getValue());
							}
						}
					} catch (URISyntaxException e) {
						log.debug("error while parsing relayState URL", e);
					}
				}
			}

		}
		return false;
	}

	private static boolean isPositiveRememberMeValue(String paramValue) {
		return (paramValue.equalsIgnoreCase("true") || paramValue.equalsIgnoreCase("on") || paramValue.equalsIgnoreCase("yes") || paramValue.equals("1"));
	}
}
