/**
 *  
 *  BibSonomy-Rest-Client - The REST-client.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.rest.RestProperties;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public abstract class HttpWorker {

	protected static final Log LOGGER = LogFactory.getLog(HttpWorker.class.getName());

	public static final String HEADER_USER_AGENT = "User-Agent";
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String HEADER_AUTH_BASIC = "Basic ";

	public static final String USER_AGENT_VALUE = RestProperties.getInstance().getApiUserAgent();
	public static final String UTF8 = "UTF-8";

	private final HttpClient httpClient;
	protected int httpResult;

	protected final String username;
	protected final String apiKey;
	
	protected String proxyHost;
	protected int proxyPort;
	

	public HttpWorker(final String username, final String apiKey) {
		this.username = username;
		this.apiKey = apiKey;
		
		this.httpClient = new HttpClient();
		final HttpClientParams httpClientParams = new HttpClientParams();
		final DefaultHttpMethodRetryHandler defaultHttpMethodRetryHandler = new DefaultHttpMethodRetryHandler(0, false);
		httpClientParams.setParameter(HEADER_USER_AGENT, USER_AGENT_VALUE);
		httpClientParams.setParameter(HttpClientParams.RETRY_HANDLER, defaultHttpMethodRetryHandler);
		httpClientParams.setParameter(HttpClientParams.HTTP_CONTENT_CHARSET, UTF8);
		httpClientParams.setAuthenticationPreemptive(true);
		
		this.httpClient.setParams(httpClientParams);
	}

	/**
	 * Encode the username and password for BASIC authentication
	 * 
	 * @return Basic + Base64 encoded(username + ':' + password)
	 */
	protected String encodeForAuthorization() {
		String retVal = HEADER_AUTH_BASIC;
		try {
			retVal += new String(Base64.encodeBase64((this.username + ":" + this.apiKey).getBytes()), UTF8 );
		} catch (UnsupportedEncodingException e1) {
			retVal += new String(Base64.encodeBase64((this.username + ":" + this.apiKey).getBytes()));
		}
		return retVal;
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
	 * @param proxyHost
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	/**
	 * @param proxyPort
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}
}