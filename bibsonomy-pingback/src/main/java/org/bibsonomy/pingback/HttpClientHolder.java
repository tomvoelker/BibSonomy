/**
 * BibSonomy Pingback - Pingback/Trackback for BibSonomy.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.pingback;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

/**
 * Returns an instance of {@link HttpClient}. The singleton pattern allows us 
 * to instanciate the HttpClient only when it is needed and to have only one 
 * instance of it.
 * 
 * That way we could later on implement/add caching of web pages.
 * 
 * @author rja
 */
public class HttpClientHolder {

	private static HttpClientHolder instance = null;
	private final HttpClient httpClient;

	/**
	 * @return the {@link HttpClientHolder} instance
	 */
	public static HttpClientHolder getInstance() {
		if (instance == null) {
			instance = new HttpClientHolder();
		}
		return instance;
	}
	
	private HttpClientHolder() {
		/*
		 * HTTP client 4.1.1
		 */
		final SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		final ThreadSafeClientConnManager conman = new ThreadSafeClientConnManager(schemeRegistry);
		// conman.setDefaultMaxPerRoute(10); // allow more than 10 connections to the same host
		this.httpClient = new DefaultHttpClient(conman);
	}
	
	/**
	 * @return the {@link HttpClient}
	 */
	public HttpClient getHttpClient() {
		return httpClient;
	}
	
}
