package org.bibsonomy.importer.bookmark.service;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.services.importer.RelationImporter;
import org.bibsonomy.services.importer.RemoteServiceBookmarkImporter;

/**
 * Creates a new instance of the {@link DeliciousImporter}.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class DeliciousImporterFactory {
	
	private static final String BUNDLES_URL_PATH = "/v1/tags/bundles/all";
	private static final String POSTS_URL_PATH = "/v1/posts/all";
	
	/*
	 * TODO: there was a reason we use "-1" as port ... please document it 
	 * here
	 */
	private static final int PORT = -1;
	private static final String HTTPS = "https";
	private static final String DELICIOUS_API_URL = "api.del.icio.us";
	
	private String userAgent;
	
	/**
	 * Default constructor using the default {@link #apiURL}.
	 *  
	 * @throws MalformedURLException
	 */
	public DeliciousImporterFactory() throws MalformedURLException {
		this.userAgent = "Wget/1.9.1";
	}
	

	public RelationImporter getRelationImporter() throws MalformedURLException {
		return new DeliciousImporter(buildURL(BUNDLES_URL_PATH), userAgent);
	}

	public RemoteServiceBookmarkImporter getBookmarkImporter() throws MalformedURLException {
		return new DeliciousImporter(buildURL(POSTS_URL_PATH), userAgent);
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
	
	private URL buildURL(final String urlPath) throws MalformedURLException {
		return new URL (HTTPS, DELICIOUS_API_URL, PORT, urlPath);
	}
}

