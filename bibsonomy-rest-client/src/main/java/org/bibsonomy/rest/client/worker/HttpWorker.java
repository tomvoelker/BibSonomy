/**
 * BibSonomy-Rest-Client - The REST-client.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.rest.client.worker;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.rest.auth.AuthenticationAccessor;
import org.bibsonomy.rest.client.util.RestClientUtils;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.utils.HeaderUtils;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
  * @param <M> the http method used by the http worker
 */
public abstract class HttpWorker<M extends HttpMethod> {
	/** the logger for all workers */
	protected static final Log LOGGER = LogFactory.getLog(HttpWorker.class);
	
	private final HttpClient httpClient;
	/** the http result code of the worker */
	protected int httpResult;

	/** the name of the user to use */
	protected final String username;
	/** the api key of the user */
	protected final String apiKey;
	/** the authenication accessor to use */
	protected final AuthenticationAccessor accessor;
	
	private RenderingFormat renderingFormat;

	/**
	 * @param username the username
	 * @param apiKey the apikey
	 * @param accessor the accessor to use (e.g. OAuthAccessor)
	 */
	public HttpWorker(final String username, final String apiKey, final AuthenticationAccessor accessor) {
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
		if (this.accessor != null) {
			return accessor.perform(url, requestBody, method, this.renderingFormat);
		}
		
		//
		// handle http basic requests
		// 
		
		// add auth header
		method.addRequestHeader(HeaderUtils.HEADER_AUTHORIZATION, HeaderUtils.encodeForAuthorization(this.username, this.apiKey));
		method.setDoAuthentication(true);
		// add accept and content type header
		final String mimeType = this.renderingFormat.getMimeType();
		method.addRequestHeader("Accept", mimeType);
		method.addRequestHeader("Content-Type", mimeType);
		
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("calling " + url + " with '" + requestBody + "'");
			}
			this.httpResult = getHttpClient().executeMethod(method);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("HTTP result: " + this.httpResult);
				LOGGER.debug("response:\n" + method.getResponseBodyAsString());
				LOGGER.debug("===================================================");
			}
			return this.readResponse(method);
		} catch (final IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ErrorPerformingRequestException(e);
		} finally {
			method.releaseConnection();
		}
	}
	
	/**
	 * @param url
	 * @param requestBody
	 * @return the {@link HttpMethod} the worker works on
	 */
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


	/**
	 * @return the renderingFormat
	 */
	protected RenderingFormat getRenderingFormat() {
		return this.renderingFormat;
	}
}