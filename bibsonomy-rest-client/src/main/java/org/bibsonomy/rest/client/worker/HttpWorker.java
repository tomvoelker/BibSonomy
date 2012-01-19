/**
 *
 *  BibSonomy-Rest-Client - The REST-client.
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

package org.bibsonomy.rest.client.worker;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.ParameterStyle;
import net.oauth.http.HttpMessage;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.util.RestClientUtils;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.utils.HeaderUtils;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 * @param <M> the http method used by the http worker
 */
public abstract class HttpWorker<M extends HttpMethod> {

	protected static final Log LOGGER = LogFactory.getLog(HttpWorker.class.getName());
	
	
	private final HttpClient httpClient;
	protected int httpResult;

	protected final String username;
	protected final String apiKey;
	protected final OAuthAccessor accessor;
	
	private RenderingFormat renderingFormat;

	/**
	 * @param username the username
	 * @param apiKey the apikey
	 */
	public HttpWorker(final String username, final String apiKey, final OAuthAccessor accessor) {
		this.username = username;
		this.apiKey = apiKey;
		this.accessor = accessor;
		
		this.httpClient = RestClientUtils.getDefaultClient();
	}


	/**
	 * @see #perform(String, String)
	 * 
	 * @param url the url to call
	 * @return a reader that holds the response from the server
	 * @throws ErrorPerformingRequestException
	 */
	public Reader perform(final String url) throws ErrorPerformingRequestException {
		return this.perform(url, null);
	}
	
	/**
	 * 
	 *  
	 * @param url  the url to call
	 * @param requestBody	the body to send
	 * @return a reader that holds the response from the server
	 * @throws ErrorPerformingRequestException
	 */
	public Reader perform(final String url, final String requestBody) throws ErrorPerformingRequestException {
		final M method = this.getMethod(url, requestBody);
		
		//
		// handle OAuth requests
		// 
		if (this.accessor!=null) {
			List<Map.Entry<?,?>> params = new ArrayList<Map.Entry<?,?>>();
			params.add(new OAuth.Parameter("oauth_token", this.accessor.accessToken));
			try {
				OAuthMessage request;
				if (present(requestBody)) {
					request = this.accessor.newRequestMessage(method.getName(), url, params, new ByteArrayInputStream(requestBody.getBytes("UTF-8")));
				} else {
					request = this.accessor.newRequestMessage(method.getName(), url, params);
				}
				Object accepted = accessor.consumer.getProperty(OAuthConsumer.ACCEPT_ENCODING);
		        if (accepted != null) {
		            request.getHeaders().add(new OAuth.Parameter(HttpMessage.ACCEPT_ENCODING, accepted.toString()));
		        }
		        request.getHeaders().add(new OAuth.Parameter("Accept", this.renderingFormat.getMimeType()));
		        request.getHeaders().add(new OAuth.Parameter("Content-Type", this.renderingFormat.getMimeType()));

		        Object ps = accessor.consumer.getProperty("parameterStyle");
		        ParameterStyle style = (ps == null) ? ParameterStyle.BODY : Enum.valueOf(ParameterStyle.class, ps.toString());
		        
		        return new StringReader(RestClientUtils.getDefaultOAuthClient().invoke(request, style).readBodyAsString());
			} catch (Exception e) {
				throw new ErrorPerformingRequestException(e);
			}
		}
		
		//
		// handle http basic requests
		// 
		
		// add auth header
		method.addRequestHeader(HeaderUtils.HEADER_AUTHORIZATION, HeaderUtils.encodeForAuthorization(this.username, this.apiKey));
		method.setDoAuthentication(true);
		// add accept and content type header
		method.addRequestHeader("Accept", this.renderingFormat.getMimeType());
		method.addRequestHeader("Content-Type", this.renderingFormat.getMimeType());
		
		try {
			this.httpResult = getHttpClient().executeMethod(method);
			LOGGER.debug("HTTP result: " + this.httpResult);
			LOGGER.debug("response:\n" + method.getResponseBodyAsString());
			LOGGER.debug("===================================================");
			return this.readResponse(method);
		} catch (final IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ErrorPerformingRequestException(e);
		} finally {
			method.releaseConnection();
		}
	}
	
	protected abstract M getMethod(final String url, final String requestBody);
	
	/**
	 * 
	 * @param method
	 * @return a reader that holds the response from the server
	 * @throws IOException
	 * @throws ErrorPerformingRequestException
	 */
	protected Reader readResponse(final M method) throws IOException, ErrorPerformingRequestException {
		return new StringReader(method.getResponseBodyAsString());
	}

	/**
	 * @return Returns the httpClient.
	 */
	protected HttpClient getHttpClient() {
		return this.httpClient;
	}

	/**
	 * @return Returns the httpResult.
	 */
	public int getHttpResult() {
		return this.httpResult;
	}
	
	/**
	 * @param renderingFormat the renderingFormat to set
	 */
	public void setRenderingFormat(final RenderingFormat renderingFormat) {
		this.renderingFormat = renderingFormat;
	}
}