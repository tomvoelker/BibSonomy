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
 * @version $Id$
 */
public class HttpClientHolder {

	private static HttpClientHolder instance = null;
	private final HttpClient httpClient;

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
		//		conman.setDefaultMaxPerRoute(10); // allow more than 10 connections to the same host
		this.httpClient = new DefaultHttpClient(conman);
		/*
		 * HTTP client 4.0.1
		 */
		/*
		final SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		final HttpParams httpParams = new BasicHttpParams(); // FIXME: not thread-safe :-(
		//        httpParams.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 25);
		//        httpParams.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, 5);
		final ThreadSafeClientConnManager conman = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
		//		conman.setDefaultMaxPerRoute(10); // allow more than 10 connections to the same host
		final HttpParams httpParams2 = new BasicHttpParams(); // FIXME: probably not thread-safe :-(
		this.httpClient = new DefaultHttpClient(conman, httpParams2);
		*/
	}	
	
	public HttpClient getHttpClient() {
		return httpClient;
	}
	
}
