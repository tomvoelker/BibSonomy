package org.bibsonomy.importer.bookmark.service;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.services.importer.RemoteServiceBookmarkImporter;
import org.bibsonomy.services.importer.RemoteServiceBookmarkImporterFactory;

/**
 * Creates a new instance of the {@link DeliciousImporter}.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class DeliciousImporterFactory implements RemoteServiceBookmarkImporterFactory {
	
	public static final String BUNDLES_URL_PATH = "/v1/tags/bundles/all";
	public static final String POSTS_URL_PATH = "/v1/posts/all";
	
	/*
	 * TODO: there was a reason we use "-1" as port ... please document it 
	 * here
	 */
	private static final int PORT = -1;
	private static final String HTTPS = "https";
	private static final String DELICIOUS_API_URL = "api.del.icio.us";
	
	private URL apiUrl;
	private String userAgent;
	
	/**
	 * Default constructor using the default {@link #apiURL}.
	 *  
	 * @throws MalformedURLException
	 */
	public DeliciousImporterFactory() throws MalformedURLException {
		buildURL(POSTS_URL_PATH);
		this.userAgent = "Wget/1.9.1";
	}
	
	
	public RemoteServiceBookmarkImporter getImporter() {
		return new DeliciousImporter(apiUrl, userAgent);
	}


	public URL getApiUrl() {
		return apiUrl;
	}


	/**
	 * The URL used to access the Delicious API.
	 * 
	 * @param apiUrl
	 */
	public void setApiUrl(URL apiUrl) {
		this.apiUrl = apiUrl;
	}


	public String getUserAgent() {
		return userAgent;
	}


	/**
	 * The user agent string the importer shall use to identify itself against
	 * the Delicious API in the corresponding HTTP header field.
	 *  
	 * @param userAgent
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	public void buildURL(String urlPath) throws MalformedURLException{
		this.apiUrl = new URL (HTTPS, DELICIOUS_API_URL, PORT, urlPath);
	}
}

