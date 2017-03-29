/**
 * BibSonomy-OpenSocial - Implementation of the Opensocial specification and OAuth Security Handling
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.opensocial.oauth;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;
import net.oauth.server.OAuthServlet;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.CharsetUtil;
import org.apache.shindig.social.core.oauth.OAuthSecurityToken;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.util.StringUtils;

/**
 * Utility class for verifying OAuth signed requests
 * 
 * @author fei
 */
public class OAuthRequestValidator {
	private final static Log log = LogFactory.getLog(OAuthRequestValidator.class);

	/**
	 * OAuth constants not found in the upstream OAuth library
	 */
	public final class OAuthConstants {
		private OAuthConstants() {}
		public static final String OAUTH_SESSION_HANDLE = "oauth_session_handle";
		public static final String OAUTH_EXPIRES_IN = "oauth_expires_in";
		public static final String OAUTH_BODY_HASH = "oauth_body_hash";

		public static final String PROBLEM_ACCESS_TOKEN_EXPIRED = "access_token_expired";
		public static final String PROBLEM_PARAMETER_MISSING = "parameter_missing";
		public static final String PROBLEM_TOKEN_INVALID = "token_invalid";
		public static final String PROBLEM_BAD_VERIFIER = "bad_verifier";
	}

	public static final String REQUESTOR_ID_PARAM = "xoauth_requestor_id";

	/**
	 * For verifying the request body we have to read the input stream. Because
	 * the servlet stream can only be read once, making the content unavailable to the receiving
	 * servlet. After reading the body, we store the raw content byte array 
	 * using request.setAttribute(STASHED_BODY, <body byte array>)
	 */
	public static final String STASHED_BODY = "STASHED_BODY";

	/** access BibSonomy's token store */
	private OAuthDataStore store;

	/**
	 * Handle an incoming OAuth request:
	 *   1) extract the OAuth message from the request
	 *   2) verify the requests signature(s)
	 *   
	 * @param request
	 * @return
	 */
	public SecurityToken getSecurityTokenFromRequest(final HttpServletRequest request) {
		final OAuthMessage message = OAuthServlet.getMessage(request, null);
		if (!present(getParameter(message, OAuth.OAUTH_SIGNATURE))) {
			// Is not an oauth request
			return null;
		}
		
		// verify the request's body
		// FIXME: this is not implemented, as we would 'consume' the request's input stream
		final String bodyHash = getParameter(message, OAuthConstants.OAUTH_BODY_HASH);
		if (present(bodyHash)) {
			// verifyBodyHash(request, bodyHash);
		}
		try {
			return this.verifyMessage(message);
		} catch (final OAuthProblemException oauthException) {
			throw new RuntimeException("OAuth Authentication Failure", oauthException);
		}
	}

	/**
	 * verify the incoming OAuth message 
	 * 
	 * @param message
	 * @return the security token
	 * @throws OAuthProblemException
	 */
	protected SecurityToken verifyMessage(final OAuthMessage message) throws OAuthProblemException {
		// retrieve the corresponding token from the token database
		final OAuthEntry entry = this.getOAuthEntry(message);
		final OAuthConsumer authConsumer = this.getConsumer(message);

		final OAuthAccessor accessor = new OAuthAccessor(authConsumer);

		if (present(entry)) {
			accessor.tokenSecret = entry.getTokenSecret();
			accessor.accessToken = entry.getToken();
		}

		try {
			final OAuthValidator validator = new SimpleOAuthValidator();
			validator.validateMessage(message, accessor);
			
			return this.getTokenFromVerifiedRequest(message, entry, authConsumer);
		} catch (final OAuthProblemException e) {
			throw e;
		} catch (final OAuthException | IOException | URISyntaxException e) {
			final OAuthProblemException ope = new OAuthProblemException(OAuth.Problems.SIGNATURE_INVALID);
			ope.setParameter(OAuth.Problems.OAUTH_PROBLEM_ADVICE, e.getMessage());
			throw ope;
		}
	}

	/**
	 * Verify request's body (http://oauth.googlecode.com/svn/spec/ext/body_hash/1.0/drafts/4/spec.html)
	 *  
	 * @param request
	 * @param oauthBodyHash
	 * @throws AccessDeniedException
	 */
	public static void verifyBodyHash(final HttpServletRequest request, final String oauthBodyHash) throws AccessDeniedException {
		// we are doing body hash signing which is not permitted for form-encoded data
		if ((request.getContentType() != null) && request.getContentType().contains(OAuth.FORM_ENCODED)) {
			throw new AccessDeniedException("Cannot use oauth_body_hash with a Content-Type of application/x-www-form-urlencoded");
		}
		try {
			final byte[] rawBody = readBody(request);
			final byte[] received = Base64.decodeBase64(CharsetUtil.getUtf8Bytes(oauthBodyHash));
			final byte[] expected = DigestUtils.sha(rawBody);
			if (!Arrays.equals(received, expected)) {
				throw new AccessDeniedException("oauth_body_hash failed verification");
			}
		} catch (final IOException ioe) {
			throw new AccessDeniedException("Unable to read content body for oauth_body_hash verification");
		}
	}

	//------------------------------------------------------------------------
	// helper functions
	//------------------------------------------------------------------------
	/**
	 * Get the requesting user's credentials from the OAuth token
	 *   
	 * @param message
	 * @param entry
	 * @param authConsumer
	 * @return
	 * @throws OAuthProblemException
	 */
	protected SecurityToken getTokenFromVerifiedRequest(final OAuthMessage message, final OAuthEntry entry, final OAuthConsumer authConsumer) throws OAuthProblemException {
		if (entry != null) {
			// sucessfully authenticated 3-legged request   
			return new OAuthSecurityToken(entry.getUserId(), entry.getCallbackUrl(), entry.getAppId(), entry.getDomain(), entry.getContainer(), entry.expiresAt().getTime());
		}
		// 2-legged request
		// TODO: not implemented
		final String userId = getParameter(message, REQUESTOR_ID_PARAM);
		return this.store.getSecurityTokenForConsumerRequest(authConsumer.consumerKey, userId);
	}

	/**
	 * retrieve the corresponding OAuth token for the incoming request from the database
	 * 
	 * @param message
	 * @return
	 * @throws OAuthProblemException
	 */
	protected OAuthEntry getOAuthEntry(final OAuthMessage message) throws OAuthProblemException {
		OAuthEntry entry = null;
		final String token = getParameter(message, OAuth.OAUTH_TOKEN);
		if (present(token))  {
			entry = this.store.getEntry(token);
			if (entry == null) {
				final OAuthProblemException e = new OAuthProblemException(OAuth.Problems.TOKEN_REJECTED);
				e.setParameter(OAuth.Problems.OAUTH_PROBLEM_ADVICE, "cannot find token");
				throw e;
			} else if (entry.getType() != OAuthEntry.Type.ACCESS) {
				final OAuthProblemException e = new OAuthProblemException(OAuth.Problems.TOKEN_REJECTED);
				e.setParameter(OAuth.Problems.OAUTH_PROBLEM_ADVICE, "token is not an access token");
				throw e;
			} else if (entry.isExpired()) {
				throw new OAuthProblemException(OAuth.Problems.TOKEN_EXPIRED);
			}
		}
		return entry;
	}

	/**
	 * retrieve the requesting client's consumer information from the database
	 * 
	 * @param message
	 * @return
	 * @throws OAuthProblemException
	 */
	protected OAuthConsumer getConsumer(final OAuthMessage message) throws OAuthProblemException {
		final String consumerKey = getParameter(message, OAuth.OAUTH_CONSUMER_KEY);
		final OAuthConsumer authConsumer = this.store.getConsumer(consumerKey);
		if (!present(authConsumer)) {
			throw new OAuthProblemException(OAuth.Problems.CONSUMER_KEY_UNKNOWN);
		}
		return authConsumer;
	}

	
	/**
	 * get the trimmed parameter from the request
	 * 
	 * @param requestMessage
	 * @param key
	 * @return the parameter value for the specified key
	 */
	public static String getParameter(final OAuthMessage requestMessage, final String key) {
		try {
			final String str = requestMessage.getParameter(key);
			return str == null ? null : str.trim();
		} catch (final IOException e) {
			return null;
		}
	}

	/**
	 * For verifying the request body we have to read the input stream. Because
	 * the servlet stream can only be read once, making the content unavailable to the receiving
	 * servlet. After reading the body, we store the raw content byte array 
	 * using request.setAttribute(STASHED_BODY, <body byte array>)
	 * @param request the request
	 * @return tue raw body
	 * @throws IOException 
	 */
	public static byte[] readBody(final HttpServletRequest request) throws IOException {
		if (present(request.getAttribute(STASHED_BODY))) {
			return (byte[])request.getAttribute(STASHED_BODY);
		}
		final byte[] rawBody = IOUtils.toByteArray(request.getInputStream());
		request.setAttribute(STASHED_BODY, rawBody);
		return rawBody;
	}


	/**
	 * FIXME: do we have already an utilty functio for this?
	 * @param s 
	 * @return UTF-8 byte array for the input string.
	 */
	public static byte[] getUtf8Bytes(final String s) {
		byte[] bb = ArrayUtils.EMPTY_BYTE_ARRAY;
		if (present(s)) {
			try {
				bb = s.getBytes(StringUtils.CHARSET_UTF_8);
			} catch (final UnsupportedEncodingException e) {
				log.error("Unsupported encoding", e);
			}
		}

		return bb;
	}

	/**
	 * @param store the store to set
	 */
	public void setStore(final OAuthDataStore store) {
		this.store = store;
	}
}
