package org.bibsonomy.webapp.util.spring.security.rememberMeServices;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;


/**
 * abstract remember me service
 * 
 * @author dzo
 */
public abstract class AbstractRememberMeServices extends org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices implements CookieBasedRememberMeServices {
	private static final String TOKEN_SIGNATURE_SEPERATOR = ":";
	
	/**
	 * default constructor
	 * @param key
	 * @param userDetailsService
	 */
	public AbstractRememberMeServices(final String key, final UserDetailsService userDetailsService) {
		super(key, userDetailsService);
	}

	protected String makeTokenSignature(final String[] values) {
		final StringBuilder sb = new StringBuilder();
		for (final String string : values) {
			sb.append(string);
			sb.append(TOKEN_SIGNATURE_SEPERATOR);
		}
		sb.append(this.getKey());
		
		// TODO: equals HashUtils.md5?
		final String data = sb.toString();
	    MessageDigest digest;
	    try {
	        digest = MessageDigest.getInstance("MD5");
	    } catch (final NoSuchAlgorithmException e) {
	        throw new IllegalStateException("No MD5 algorithm available!");
	    }

	    return new String(Hex.encode(digest.digest(data.getBytes())));
	}

	protected long getExpiryTime(final String cookieString) {
		long tokenExpiryTime;
		try {
	        tokenExpiryTime = new Long(cookieString).longValue();
	    } catch (final NumberFormatException nfe) {
	        throw new InvalidCookieException("Cookie token did not contain a valid number (contained '" + cookieString + "')");
	    }
	
	    if (this.isTokenExpired(tokenExpiryTime)) {
	        throw new InvalidCookieException("Cookie token has expired (expired on '"  + new Date(tokenExpiryTime) + "'; current time is '" + new Date() + "')");
	    }
		return tokenExpiryTime;
	}

	protected boolean isTokenExpired(final long tokenExpiryTime) {
		return tokenExpiryTime < System.currentTimeMillis();
	}

	protected long calculateExpiryTime(final int tokenLifetime) {
		long expiryTime = System.currentTimeMillis();
	    
		// SEC-949
	    expiryTime += 1000L * (tokenLifetime < 0 ? TWO_WEEKS_S : tokenLifetime);
	    return expiryTime;
	}
	
	@Override
	public String getCookieName() {
		return super.getCookieName();
	}
}
