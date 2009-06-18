package org.bibsonomy.importer.bookmark.service;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	private static final Log log = LogFactory.getLog(DeliciousImporterFactory.class);
	
	private URL apiUrl;
	private String userAgent;
	
	/**
	 * Default constructor using the default {@link #apiURL}.
	 *  
	 * @throws MalformedURLException
	 */
	public DeliciousImporterFactory() throws MalformedURLException {
		/*
		 * TODO: there was a reason we use "-1" as port ... please document it 
		 * here
		 */
		this.apiUrl = new URL ("https", "api.del.icio.us", -1, "/v1/posts/all");
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
}

