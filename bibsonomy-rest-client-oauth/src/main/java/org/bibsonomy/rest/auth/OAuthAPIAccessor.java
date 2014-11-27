/**
 * BibSonomy-Rest-Client-OAuth - The REST-client OAuth Accessor.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

package org.bibsonomy.rest.auth;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.ParameterStyle;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient3.HttpClient3;
import net.oauth.http.HttpMessageDecoder;

import org.apache.commons.httpclient.HttpMethod;
import org.bibsonomy.rest.client.RestLogicFactory;
import org.bibsonomy.rest.client.util.RestClientUtils;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.rest.renderer.RenderingFormat;

/**
 * TODO: add integration test
 * 
 * implements OAuth authenticated access for REST-API
 * 
 * Workflow:
 * 
 *   1) String redirectUrl = accessor.getAuthorizationUrl()
 *   2) redirect user to redirectUrl
 *   3) set the request token ({@link #setRequestToken(String)})
 *   4) call accessor.obtainAccessToken()
 *   5) get logic interface using the accessor @see {@link RestLogicFactory}
 * 
 * @author fei
 */
public class OAuthAPIAccessor implements AuthenticationAccessor {
	/** global OAuth client */
	private static final OAuthClient OAUTH_CLIENT = new OAuthClient(new HttpClient3());
	
	/** end points */
	/** url for obtaining (temporary) request tokens */
	public static final String OAUTH_REQUEST_URL = "oauth/requestToken";
	/** url for authorizing request tokens */
	public static final String OAUTH_AUTHORIZATION_URL = "oauth/authorize";
	/** url for obtaining access tokens from previously authorized request tokens */
	public static final String OAUTH_ACCESS_URL = "oauth/accessToken";
	
	
	/** actual OAuth communication implementation */
	private final OAuthAccessor accessor;
	
	/** remote user id */
	private String userId;
	
	/**
	 * constructor
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 * @param callbackUrl
	 */
	public OAuthAPIAccessor(final String consumerKey, final String consumerSecret, final String callbackUrl) {
		this(RestLogicFactory.BIBSONOMY_URL, consumerKey, consumerSecret, callbackUrl);
	}
	
	/**
	 * constructor
	 * 
	 * @param projectHome
	 * @param consumerKey
	 * @param consumerSecret
	 * @param callbackUrl
	 */
	public OAuthAPIAccessor(final String projectHome, final String consumerKey, final String consumerSecret, final String callbackUrl) {
		final OAuthServiceProvider provider = new OAuthServiceProvider(projectHome + OAUTH_REQUEST_URL, projectHome + OAUTH_AUTHORIZATION_URL, projectHome + OAUTH_ACCESS_URL);
		final OAuthConsumer consumer = new OAuthConsumer(callbackUrl, consumerKey, consumerSecret, provider);
		this.accessor = new OAuthAccessor(consumer);
	}
	
	/**
	 * constructor 
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 * @param callbackUrl
	 * @param accessToken
	 * @param tokenSecret
	 */
	public OAuthAPIAccessor(final String consumerKey, final String consumerSecret, final String callbackUrl, final String accessToken, final String tokenSecret) {
		this(RestLogicFactory.BIBSONOMY_URL, consumerKey, consumerSecret, callbackUrl, accessToken, tokenSecret);
	}
	
	/**
	 * constructor 
	 * 
	 * @param projectHome 
	 * @param consumerKey
	 * @param consumerSecret
	 * @param callbackUrl
	 * @param accessToken
	 * @param tokenSecret
	 */
	public OAuthAPIAccessor(final String projectHome, final String consumerKey, final String consumerSecret, final String callbackUrl, final String accessToken, final String tokenSecret) {
		this(projectHome, consumerKey, consumerSecret, callbackUrl);
		this.accessor.accessToken = accessToken;
		this.accessor.tokenSecret = tokenSecret;
	}
	
	/**
	 * constructor
	 * 
	 * @param accessor
	 */
	public OAuthAPIAccessor(final OAuthAccessor accessor) {
		this.accessor = accessor;
	}
	
	/**
	 * step one: obtain request token and return url which the user must visit for 
	 *           authorizing the request token
	 * 
	 * @return the auth url
	 * @throws IOException
	 * @throws OAuthException
	 * @throws URISyntaxException
	 */
	public String getAuthorizationUrl() throws IOException, OAuthException, URISyntaxException {
		/*
		 * obtain (temporary) request token
		 */
		List<OAuth.Parameter> callback = null;
		if (present(this.accessor.consumer.callbackURL)) {
			callback = OAuth.newList(OAuth.OAUTH_CALLBACK, this.accessor.consumer.callbackURL);
		}
		OAUTH_CLIENT.getRequestToken(this.accessor, null, callback);
		
		return this.accessor.consumer.serviceProvider.userAuthorizationURL + "?oauth_token=" + this.accessor.requestToken;
	}
	
	/**
	 * step two: if the user authorized the previously obtained request token, finally get
	 *           an access token for future use
	 *            
	 * @throws IOException
	 * @throws OAuthException
	 * @throws URISyntaxException
	 */
	public void obtainAccessToken() throws IOException, OAuthException, URISyntaxException {
		/*
		 * transform previously authorized request token to an access token
		 */
		final Properties paramProps = new Properties();
		paramProps.setProperty("oauth_token", this.accessor.requestToken);
		final List<Map.Entry<?, ?>> params = new ArrayList<Map.Entry<?, ?>>();
		final Iterator<?> it = paramProps.entrySet().iterator();
		while (it.hasNext()) {
			final Map.Entry<?, ?> p = (Map.Entry<?, ?>) it.next();
			params.add(new OAuth.Parameter((String)p.getKey(), (String) p.getValue()));
		}
		
		final OAuthMessage response = OAUTH_CLIENT.invoke(this.accessor, "GET",  this.accessor.consumer.serviceProvider.accessTokenURL, params);
		// set access token and token secret
		this.accessor.accessToken = response.getParameter("oauth_token");
		this.accessor.tokenSecret = response.getParameter("oauth_token_secret");
		
		// the logged in user
		this.userId = response.getParameter("user_id");
	}
	
	@Override
	public <M extends HttpMethod> Reader perform(final String url, final String requestBody, final M method, final RenderingFormat renderingFormat) throws ErrorPerformingRequestException {
		final List<Map.Entry<?, ?>> params = new ArrayList<Map.Entry<?, ?>>();
		params.add(new OAuth.Parameter("oauth_token", this.accessor.accessToken));
		try {
			OAuthMessage request;
			if (present(requestBody)) {
				request = this.accessor.newRequestMessage(method.getName(), url, params, new ByteArrayInputStream(requestBody.getBytes(RestClientUtils.CONTENT_CHARSET)));
			} else {
				request = this.accessor.newRequestMessage(method.getName(), url, params);
			}
			final Object accepted = this.accessor.consumer.getProperty(OAuthConsumer.ACCEPT_ENCODING);
			if (accepted != null) {
				request.getHeaders().add(new OAuth.Parameter(HttpMessageDecoder.ACCEPT_ENCODING, accepted.toString()));
			}
			request.getHeaders().add(new OAuth.Parameter("Accept", renderingFormat.getMimeType()));
			request.getHeaders().add(new OAuth.Parameter("Content-Type", renderingFormat.getMimeType()));
			
			final Object ps = this.accessor.consumer.getProperty("parameterStyle");
			final ParameterStyle style = (ps == null) ? ParameterStyle.BODY : Enum.valueOf(ParameterStyle.class, ps.toString());
			
			return new StringReader(OAUTH_CLIENT.invoke(request, style).readBodyAsString());
		} catch (final Exception e) {
			throw new ErrorPerformingRequestException(e);
		}
	}
	
	/**
	 * @param requestToken the request token from step to set
	 */
	public void setRequestToken(final String requestToken) {
		this.accessor.requestToken = requestToken;
	}

	/**
	 * @return the OAuth access token
	 */
	public String getAccessToken() {
		return this.accessor.accessToken;
	}

	/**
	 * @return the OAuth token secret
	 */
	public String getTokenSecret() {
		return this.accessor.tokenSecret;
	}
	
	/**
	 * get remote id of the authenticated user
	 * 
	 * @return remote user id if successfully authenticated, null otherwise
	 */
	public String getRemoteUserId() {
		return this.userId;
	}
}
