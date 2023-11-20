/**
 * BibSonomy-Rest-Client - The REST-client.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.bibsonomy.rest.client.auth.AuthenticationAccessor;
import org.bibsonomy.rest.client.util.RestClientUtils;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.rest.renderer.RenderingFormat;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
  * @param <M> the http method used by the http worker
 */
public abstract class HttpWorker<M extends HttpRequestBase> {
	/** the logger for all workers */
	protected static final Log LOGGER = LogFactory.getLog(HttpWorker.class);
	
	private final CloseableHttpClient httpClient;
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
		try {
			// add accept and content type header
			final String mimeType = this.renderingFormat.getMimeType();
			method.addHeader("Accept", mimeType);
			method.addHeader("Content-Type", mimeType);
			method.addHeader("Connection", "close"); // we close the connection after we got the response from the server

			// add auth header
			final UsernamePasswordCredentials creds = new UsernamePasswordCredentials(this.username, this.apiKey);
			final HttpClientContext context = HttpClientContext.create();
			method.addHeader(new BasicScheme(Charset.forName(RestClientUtils.CONTENT_CHARSET)).authenticate(creds, method, context));

			LOGGER.debug("calling " + url + " with '" + requestBody + "'");

			final CloseableHttpResponse response = this.getHttpClient().execute(method, context);
			this.httpResult = response.getStatusLine().getStatusCode();

			LOGGER.debug("HTTP result: " + this.httpResult);
			try {
				return this.readResponse(response);
			} finally {
				response.close();
			}
		} catch (final IOException | AuthenticationException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ErrorPerformingRequestException(e);
		} finally {
			method.releaseConnection();
		}
	}
	
	/**
	 * @param url
	 * @param requestBody
	 * @return the {@link HttpRequestBase} the worker works on
	 */
	protected abstract M getMethod(final String url, final String requestBody);
	
	/**
	 * 
	 * @param method
	 * @return a reader that holds the response from the server
	 * @throws IOException
	 * @throws ErrorPerformingRequestException
	 */
	protected Reader readResponse(final HttpResponse method) throws IOException {
		return new StringReader(EntityUtils.toString(method.getEntity()));
	}

	/**
	 * @return Returns the httpClient.
	 */
	protected CloseableHttpClient getHttpClient() {
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