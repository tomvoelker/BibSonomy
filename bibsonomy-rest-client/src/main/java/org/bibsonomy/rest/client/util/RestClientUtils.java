/**
 * BibSonomy-Rest-Client - The REST-client.
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
package org.bibsonomy.rest.client.util;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.utils.HeaderUtils;
import org.bibsonomy.util.BasicUtils;
import org.bibsonomy.util.StringUtils;

/**
 * @author dzo
 * @author agr
 */
public class RestClientUtils {
	
	/** the content charset used by the rest client */
	public static final String CONTENT_CHARSET = StringUtils.CHARSET_UTF_8;

	/** the connection timeout */
	private static final int CONNECTION_TIMEOUT = 5 * 1000;

	/** the read timeout */
	private static final int READ_TIMEOUT = 5 * 1000;

	/**
	 * according to http://hc.apache.org/httpclient-3.x/threading.html
	 * HttpClient is thread safe and we can use one instance for several requests.
	 */
	private static final MultiThreadedHttpConnectionManager CONNECTION_MANAGER = new MultiThreadedHttpConnectionManager();
	private static final HttpClient CLIENT;

	/**
	 * @return the client
	 */
	public static HttpClient getDefaultClient() {
		return CLIENT;
	}

	static {
		final HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setConnectionTimeout(CONNECTION_TIMEOUT);
		params.setSoTimeout(READ_TIMEOUT);
		CONNECTION_MANAGER.setParams(params);
		CLIENT = new HttpClient(CONNECTION_MANAGER);

		/*
		 * config http client for requests
		 */
		final HttpClientParams httpClientParams = new HttpClientParams();
		final DefaultHttpMethodRetryHandler defaultHttpMethodRetryHandler = new DefaultHttpMethodRetryHandler(0, false);
		httpClientParams.setParameter(HeaderUtils.HEADER_USER_AGENT, RESTConfig.API_USER_AGENT + "_" + BasicUtils.VERSION);
		httpClientParams.setParameter(HttpClientParams.RETRY_HANDLER, defaultHttpMethodRetryHandler);
		httpClientParams.setParameter(HttpClientParams.HTTP_CONTENT_CHARSET, RestClientUtils.CONTENT_CHARSET);
		httpClientParams.setAuthenticationPreemptive(true);
		
		CLIENT.setParams(httpClientParams);
		
		// proxy
		final String proxyHost = System.getProperty("http.proxyHost");
		if (present(proxyHost)){
			final String proxyPortString = System.getProperty("http.proxyPort");
			int proxyPort = 80;
			if (present(proxyPortString)) {
				proxyPort = Integer.parseInt(proxyPortString);
			}
			
			CLIENT.getHostConfiguration().setProxy(proxyHost, proxyPort);
		}
	}
}
