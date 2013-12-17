/**
 *
 *  BibSonomy-Importer - Various importers for bookmarks and publications.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.importer.bookmark.service;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.services.importer.RelationImporter;
import org.bibsonomy.services.importer.RemoteServiceBookmarkImporter;

/**
 * Creates a new instance of the {@link DeliciousImporter}.
 * 
 * @author:  rja
 * 
 */
public class DeliciousImporterFactory {
	
	private String bundlesPath = "/v1/tags/bundles/all";
	private String postsPath   = "/v1/posts/all";
	
	/** the port to use -1 => use the default port of the protocol*/
	private int port = -1;
	private String protocol = "https";
	private String host = "api.del.icio.us";
	
	private String userAgent = "Wget/1.9.1";
	
	/**
	 * @return the relation importer
	 * @throws MalformedURLException
	 */
	public RelationImporter getRelationImporter() throws MalformedURLException {
		return new DeliciousImporter(buildURL(bundlesPath), userAgent);
	}
	
	/**
	 * @return the book
	 * @throws MalformedURLException
	 */
	public RemoteServiceBookmarkImporter getBookmarkImporter() throws MalformedURLException {
		return new DeliciousImporter(buildURL(postsPath), userAgent);
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
		return new URL(protocol, host, port, path);
	}

	/**
	 * @param bundlesPath the bundlesPath to set
	 */
	public void setBundlesPath(String bundlesPath) {
		this.bundlesPath = bundlesPath;
	}

	/**
	 * @param postsPath the postsPath to set
	 */
	public void setPostsPath(String postsPath) {
		this.postsPath = postsPath;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
}

