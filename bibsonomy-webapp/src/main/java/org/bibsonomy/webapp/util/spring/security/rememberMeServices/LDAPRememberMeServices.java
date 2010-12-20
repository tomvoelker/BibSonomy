package org.bibsonomy.webapp.util.spring.security.rememberMeServices;

import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.spring.security.UserAdapter;
import org.jasypt.util.text.TextEncryptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;

/**
 * @author dzo
 * @version $Id$
 */
public class LDAPRememberMeServices extends AbstractRememberMeServices {
	private static final Log log = LogFactory.getLog(LDAPRememberMeServices.class);
	
	private TextEncryptor encryptor;
	
	@Override
	protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) throws RememberMeAuthenticationException, UsernameNotFoundException {
		if (cookieTokens.length != 5) {
            throw new InvalidCookieException("Cookie token did not contain 5 tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }
		
		final String username = cookieTokens[0];
        
        
		long tokenExpiryTime = this.getExpiryTime(cookieTokens[3]);
		
		final UserDetails loadUserByUsername = this.getUserDetailsService().loadUserByUsername(username);
		if (loadUserByUsername instanceof UserAdapter) {
			final UserAdapter adapter = (UserAdapter) loadUserByUsername;
			final User user = adapter.getUser();
			final String ldapID = user.getLdapId();
			final String clearPassword = cookieTokens[2];
			
			final String expectedTokenSignature = this.makeTokenSignature(new String[] { Long.toString(tokenExpiryTime) , username, ldapID, clearPassword });
			final String signature = cookieTokens[4];
			
			if (!expectedTokenSignature.equals(signature)) {
	            throw new InvalidCookieException("Cookie token[4] contained signature '" + signature  + "' but expected '" + expectedTokenSignature + "'");
	        }
			
			user.setPassword(clearPassword);
			return loadUserByUsername;
		}
		
		throw new UsernameNotFoundException(""); // TODO
	}
	
	@Override
	protected Authentication createSuccessfulAuthentication(HttpServletRequest request, UserDetails userDetails) {
		final User user = ((UserAdapter) userDetails).getUser();
		
		// return an usernamePasswordAuthenticationToken that the ldap authentication provider can handle the token
		return new UsernamePasswordAuthenticationToken(user.getLdapId(), user.getPassword());
	}

	@Override
	protected void onLoginSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication successfulAuthentication) {
		if (successfulAuthentication instanceof UsernamePasswordAuthenticationToken) {
			final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) successfulAuthentication;
			
			final Object principal = token.getPrincipal();
			
			if (principal instanceof UserAdapter) {
				final UserAdapter userDetails = (UserAdapter) principal;
				final String username = userDetails.getUsername();
				final String ldapID = userDetails.getUser().getLdapId();
				final String clearPassword = token.getCredentials().toString();
				
				final int tokenLifetime = this.getTokenValiditySeconds();
				final long expiryTime = this.calculateExpiryTime(tokenLifetime);
				
				final String signatureValue = this.makeTokenSignature(new String[] { Long.toString(expiryTime) , username, ldapID, clearPassword });
				
				this.setCookie(new String[] { username, ldapID, clearPassword, Long.toString(expiryTime), signatureValue}, tokenLifetime, request, response);

		        if (log.isDebugEnabled()) {
		            log.debug("Added remember-me cookie for user '" + username + "', expiry: '"  + new Date(expiryTime) + "'");
		        }
			}
		}
	}
	
	@Override
	protected String encodeCookie(String[] cookieTokens) {
		return this.encryptor.encrypt(super.encodeCookie(cookieTokens));
	}

	@Override
	protected String[] decodeCookie(String cookieValue) throws InvalidCookieException {
		return super.decodeCookie(this.encryptor.decrypt(cookieValue));
	}

	/**
	 * @param encryptor The encryptor to encrypt cookies.
	 */
	public void setEncryptor(final TextEncryptor encryptor) {
		this.encryptor = encryptor;
	}
}
