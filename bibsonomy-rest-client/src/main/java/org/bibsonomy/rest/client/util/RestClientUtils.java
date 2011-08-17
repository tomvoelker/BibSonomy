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

package org.bibsonomy.rest.client.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.utils.HeaderUtils;

/**
 * @author dzo
 * @version $Id$
 */
public class RestClientUtils {
	private static final Log log = LogFactory.getLog(RestClientUtils.class);
	
	private static final String USER_AGENT_VALUE = RestProperties.getInstance().getApiUserAgent();
	
	/**
	 * the content charset used by the rest client
	 */
	public static final String CONTENT_CHARSET = "UTF-8";
	
	private static final String PROPERTIES_FILE_NAME = "bibsonomy-rest-client.properties";
	private static final String PROPERTIES_VERSION_KEY = "version";
	
	private static final HttpClient CLIENT = new HttpClient();
	
	/**
	 * @return the client
	 */
	public static HttpClient getDefaultClient() {
		return CLIENT;
	}

	static {
		String clientVersion = "unknown";
		/*
		 * load version of client from properties file
		 */
		try {
			final Properties properties = new Properties();
			
			final InputStream stream = RestClientUtils.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
			properties.load(stream);
			stream.close();
			
			clientVersion = properties.getProperty(PROPERTIES_VERSION_KEY);
		} catch (final IOException ex) {
			log.error("could not load version", ex);
		}
		
		/*
		 * config http client for requests
		 */
		final HttpClientParams httpClientParams = new HttpClientParams();
		final DefaultHttpMethodRetryHandler defaultHttpMethodRetryHandler = new DefaultHttpMethodRetryHandler(0, false);
		httpClientParams.setParameter(HeaderUtils.HEADER_USER_AGENT, USER_AGENT_VALUE + "_" + clientVersion);
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
