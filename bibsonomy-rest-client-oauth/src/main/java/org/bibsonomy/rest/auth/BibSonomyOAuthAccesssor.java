/**
 *
 *  BibSonomy-Rest-Client-OAuth - The REST-client.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
import net.oauth.http.HttpMessage;

import org.apache.commons.httpclient.HttpMethod;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.rest.renderer.RenderingFormat;

/**
 * implements OAuth authenticated access for BibSonomy's Rest-Api
 * 
 * Workflow:
 *   1) String redirectUrl = accessor.getAuthorizationUrl()
 *   2) redirect user to redirectUrl
 *   3) call accessor.obtainAccessToken()
 *   4) get logic interface using the accessor
 * 
 * @author fei
 * @version $Id$
 */
public class BibSonomyOAuthAccesssor implements AuthenticationAccessor {
	/** actual OAuth communication implementation */
	private OAuthAccessor accessor;

	/** global OAuth client */
	private static final OAuthClient OAUTH_CLIENT = new OAuthClient(new net.oauth.client.httpclient3.HttpClient3());
	
	/** url for obtaining (temporary) request tokens */
	public static final String OAUTH_REQUEST_URL = "http://opensocial.bibsonomy.org/oauth/requestToken";
	/** url for authorizing request tokens */
	public static final String OAUTH_AUTHORIZATION_URL = "http://opensocial.bibsonomy.org/oauth/authorize";
	/** url for obtaining access tokens from previously authorized request tokens */
	public static final String OAUTH_ACCESS_URL = "http://opensocial.bibsonomy.org/oauth/accessToken";
	
	/** remote user id */
	private String userId;
	
	/**
	 * constructor
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 * @param callbackUrl
	 */
	public BibSonomyOAuthAccesssor(final String consumerKey, final String consumerSecret, final String callbackUrl) {
		OAuthServiceProvider provider = new OAuthServiceProvider(OAUTH_REQUEST_URL, OAUTH_AUTHORIZATION_URL, OAUTH_ACCESS_URL);
		OAuthConsumer consumer = new OAuthConsumer(callbackUrl, consumerKey, consumerSecret, provider);

		// for implementing RSA public key authentication:
        //
        // consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.RSA_SHA1);
		// consumer.setProperty(RSA_SHA1.PRIVATE_KEY, consumerSecret);
		
        OAuthAccessor accessor = new OAuthAccessor(consumer);
		this.accessor = accessor;
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
	public BibSonomyOAuthAccesssor(final String consumerKey, final String consumerSecret, final String callbackUrl, final String accessToken, final String tokenSecret) {
		this(consumerKey, consumerSecret, callbackUrl);
		this.accessor.accessToken = accessToken;
		this.accessor.tokenSecret = tokenSecret;
	}
	
	/**
	 * constructor
	 * 
	 * @param accessor
	 */
	public BibSonomyOAuthAccesssor(OAuthAccessor accessor) {
		this.accessor = accessor;
	}
	
	//------------------------------------------------------------------------
	// OAuth specific interface
	//------------------------------------------------------------------------
	/**
	 * step one: obtain request token and return url which the user must visit for 
	 *           authorizing the request token
	 * 
	 * @return
	 * @throws IOException
	 * @throws OAuthException
	 * @throws URISyntaxException
	 */
	public String getAuthorizationUrl() throws IOException, OAuthException, URISyntaxException {
		this.execute("request");
		return OAUTH_AUTHORIZATION_URL + "?oauth_token=" + this.accessor.requestToken;
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
		this.execute("access");
	}

	/**
	 * get the access token
	 * @return
	 */
	public String getAccessToken() {
		return this.accessor.accessToken;
	}

	/**
	 * get the token secret
	 * @return
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
		
	//------------------------------------------------------------------------
	// AuthenticationAccessor interface implementation
	//------------------------------------------------------------------------
	@Override
	public <M extends HttpMethod> Reader perform(final String url, final String requestBody, final M method, final RenderingFormat renderingFormat) throws ErrorPerformingRequestException {
		List<Map.Entry<?,?>> params = new ArrayList<Map.Entry<?,?>>();
		params.add(new OAuth.Parameter("oauth_token", this.getAccessor().accessToken));
		try {
			OAuthMessage request;
			if (present(requestBody)) {
				request = this.getAccessor().newRequestMessage(method.getName(), url, params, new ByteArrayInputStream(requestBody.getBytes("UTF-8")));
			} else {
				request = this.getAccessor().newRequestMessage(method.getName(), url, params);
			}
			Object accepted = getAccessor().consumer.getProperty(OAuthConsumer.ACCEPT_ENCODING);
		    if (accepted != null) {
		        request.getHeaders().add(new OAuth.Parameter(HttpMessage.ACCEPT_ENCODING, accepted.toString()));
		    }
		    request.getHeaders().add(new OAuth.Parameter("Accept", renderingFormat.getMimeType()));
		    request.getHeaders().add(new OAuth.Parameter("Content-Type", renderingFormat.getMimeType()));

		    Object ps = getAccessor().consumer.getProperty("parameterStyle");
		    ParameterStyle style = (ps == null) ? ParameterStyle.BODY : Enum.valueOf(ParameterStyle.class, ps.toString());
		    
		    return new StringReader(OAUTH_CLIENT.invoke(request, style).readBodyAsString());
		} catch (Exception e) {
			throw new ErrorPerformingRequestException(e);
		}
	}
	
	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	/**
	 * perform OAuth requests
	 * 
	 * @param operation
	 * @throws IOException
	 * @throws OAuthException
	 * @throws URISyntaxException
	 */
	private void execute(String operation) throws IOException, OAuthException, URISyntaxException {
		if ("request".equals(operation)) {
			//
			// obtain (temporary) request token
			//
			OAUTH_CLIENT.getRequestToken(accessor);
		}
		else if ("access".equals(operation)) {
			//
			// transform previously authorized request token to an access token
			//
			Properties paramProps = new Properties();
			paramProps.setProperty("oauth_token", this.accessor.requestToken);
			OAuthMessage response = sendRequest(paramProps, OAUTH_ACCESS_URL);
			
			this.accessor.accessToken = response.getParameter("oauth_token");
			this.accessor.tokenSecret = response.getParameter("oauth_token_secret");
			this.userId = response.getParameter("user_id");
		} else {
			//
			// access the resource
			//
			Properties paramProps = new Properties();
			paramProps.setProperty("oauth_token", this.accessor.accessToken);

			OAuthMessage response = sendRequest(paramProps, operation);
			System.out.println(response.readBodyAsString());
		}
	}

	/**
	 * actually send request
	 * 
	 * @param map
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws OAuthException
	 */
	private OAuthMessage sendRequest(Map<?,?> map, String url) throws IOException, URISyntaxException, OAuthException {
		List<Map.Entry<?,?>> params = new ArrayList<Map.Entry<?,?>>();
		Iterator<?> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<?,?> p = (Map.Entry<?,?>) it.next();
			params.add(new OAuth.Parameter((String)p.getKey(), (String)p.getValue()));
		}
		return OAUTH_CLIENT.invoke(this.accessor, "GET",  url, params);
	}

	
	public OAuthAccessor getAccessor() {
		return accessor;
	}

	public void setAccessor(OAuthAccessor accessor) {
		this.accessor = accessor;
	}

}
