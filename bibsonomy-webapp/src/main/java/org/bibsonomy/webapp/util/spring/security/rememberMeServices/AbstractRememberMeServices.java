package org.bibsonomy.webapp.util.spring.security.rememberMeServices;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.springframework.security.core.codec.Hex;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;


/**
 * @author dzo
 * @version $Id$
 */
public abstract class AbstractRememberMeServices extends org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices {
	private static final String TOKEN_SIGNATURE_SEPERATOR = ":";
	
	protected String makeTokenSignature(final String[] values) {
		final StringBuilder sb = new StringBuilder();
		for (String string : values) {
			sb.append(string);
			sb.append(TOKEN_SIGNATURE_SEPERATOR);
		}
		sb.append(this.getKey());
		
		final String data = sb.toString();
	    MessageDigest digest;
	    try {
	        digest = MessageDigest.getInstance("MD5");
	    } catch (NoSuchAlgorithmException e) {
	        throw new IllegalStateException("No MD5 algorithm available!");
	    }

	    return new String(Hex.encode(digest.digest(data.getBytes())));
	}

	protected long getExpiryTime(final String cookieString) {
		long tokenExpiryTime;
		try {
	        tokenExpiryTime = new Long(cookieString).longValue();
	    } catch (NumberFormatException nfe) {
	        throw new InvalidCookieException("Cookie token did not contain a valid number (contained '" + cookieString + "')");
	    }
	
	    if (isTokenExpired(tokenExpiryTime)) {
	        throw new InvalidCookieException("Cookie token has expired (expired on '"  + new Date(tokenExpiryTime) + "'; current time is '" + new Date() + "')");
	    }
		return tokenExpiryTime;
	}

	protected boolean isTokenExpired(long tokenExpiryTime) {
		return tokenExpiryTime < System.currentTimeMillis();
	}

	protected long calculateExpiryTime(final int tokenLifetime) {
		long expiryTime = System.currentTimeMillis();
	    
		// SEC-949
	    expiryTime += 1000L * (tokenLifetime < 0 ? TWO_WEEKS_S : tokenLifetime);
	    return expiryTime;
	}
}
