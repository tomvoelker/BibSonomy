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

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.util.BasicUtils;
import org.bibsonomy.util.StringUtils;

/**
 * @author dzo
 */
public class RestClientUtils {

	private static final String CONTENT_TYPE = "multipart/form-data";
	
	/** the content charset used by the rest client */
	public static final String CONTENT_CHARSET = StringUtils.CHARSET_UTF_8;

	/** the connection timeout */
	private static final int CONNECTION_TIMEOUT = 5 * 1000;

	/** the read timeout */
	private static final int READ_TIMEOUT = 5 * 1000;

	/** thread safe pooling connection manager */
	private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();

	private static final RequestConfig DEFAULT_REQUEST_CONFIG = RequestConfig.custom()
					.setConnectTimeout(CONNECTION_TIMEOUT)
					.setSocketTimeout(READ_TIMEOUT)
					.setConnectionRequestTimeout(READ_TIMEOUT).build();


	private static final CloseableHttpClient CLIENT = buildClient(DEFAULT_REQUEST_CONFIG);

	/**
	 * @return the client
	 */
	public static CloseableHttpClient getDefaultClient() {
		return CLIENT;
	}

	/**
	 * @return a builder with the default request config
	 */
	public static RequestConfig.Builder createRequestConfigBuilder() {
		return RequestConfig.copy(DEFAULT_REQUEST_CONFIG);
	}

	/**
	 * build client based on request config
	 * @param requestConfig
	 * @return
	 */
	public static CloseableHttpClient buildClient(final RequestConfig requestConfig) {
		/*
		 * configure client
		 */
		final HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setDefaultRequestConfig(requestConfig);
		builder.setConnectionManager(CONNECTION_MANAGER);
		builder.setUserAgent(RESTConfig.API_USER_AGENT + "_" + BasicUtils.VERSION);
		builder.setRedirectStrategy(new LaxRedirectStrategy()); // to enable following redirects for POST requests

		final DefaultHttpRequestRetryHandler defaultHttpMethodRetryHandler = new DefaultHttpRequestRetryHandler(0, false);
		builder.setRetryHandler(defaultHttpMethodRetryHandler);

		// proxy
		final String proxyHost = System.getProperty("http.proxyHost");
		if (present(proxyHost)){
			final String proxyPortString = System.getProperty("http.proxyPort");
			int proxyPort = 80;
			if (present(proxyPortString)) {
				proxyPort = Integer.parseInt(proxyPortString);
			}

			final HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			final DefaultProxyRoutePlanner proxyRoutePlanner = new DefaultProxyRoutePlanner(proxy);
			builder.setRoutePlanner(proxyRoutePlanner);
		}

		// build client
		return builder.build();
	}

	/**
	 * sets the rewuest entity and other stuff
	 * @param put
	 * @param requestBody
	 */
	public static void prepareHttpMethod(final HttpEntityEnclosingRequestBase put, String requestBody) {
		put.setEntity(new StringEntity(requestBody, ContentType.create(CONTENT_TYPE, CONTENT_CHARSET)));
	}
}
