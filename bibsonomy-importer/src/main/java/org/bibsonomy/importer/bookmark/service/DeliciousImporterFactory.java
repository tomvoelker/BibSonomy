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
	
	private String bundlesPath = "/v1/tags/bundles/all";
	private String postsPath   = "/v1/posts/all";
	
	/*
	 * TODO: there was a reason we use "-1" as port ... please document it 
	 * here
	 */
	private int port = -1;
	private String protocol = "https";
	private String host = "api.del.icio.us";
	
	private String userAgent = "Wget/1.9.1";
	

	public RelationImporter getRelationImporter() throws MalformedURLException {
		return new DeliciousImporter(buildURL(bundlesPath), userAgent);
	}

	public RemoteServiceBookmarkImporter getBookmarkImporter() throws MalformedURLException {
		return new DeliciousImporter(buildURL(postsPath), userAgent);
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
	
	private URL buildURL(final String path) throws MalformedURLException {
		return new URL (protocol, host, port, path);
	}

	public String getBundlesPath() {
		return bundlesPath;
	}

	public void setBundlesPath(String bundlesPath) {
		this.bundlesPath = bundlesPath;
	}

	public String getPostsPath() {
		return postsPath;
	}

	public void setPostsPath(String postsPath) {
		this.postsPath = postsPath;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}

