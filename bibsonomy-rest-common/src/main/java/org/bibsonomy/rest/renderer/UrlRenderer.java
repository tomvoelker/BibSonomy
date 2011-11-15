/**
 *
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
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

package org.bibsonomy.rest.renderer;

import org.bibsonomy.rest.RESTConfig;

/** 
 * This renderer creates URLs according to BibSonomys REST URL scheme.
 * 
 * @author rja
 * @version $Id$
 */
public class UrlRenderer {
	private static final String PARTS_DELIMITER = "/";
	
	private final String userUrlPrefix;
	private final String groupUrlPrefix;
	private final String tagUrlPrefix;
	
	private final String postsUrlDelimiter;
	private final String documentsUrlDelimiter;
	
	private final String apiUrl;
	
	/**
	 * creates a new url renderer
	 * @param apiUrl
	 */
	public UrlRenderer(final String apiUrl) {
		this.apiUrl = apiUrl;
		this.userUrlPrefix = apiUrl + RESTConfig.USERS_URL + PARTS_DELIMITER;
		this.groupUrlPrefix = apiUrl + RESTConfig.GROUPS_URL + PARTS_DELIMITER;
		this.tagUrlPrefix = apiUrl + RESTConfig.TAGS_URL + PARTS_DELIMITER;
		this.postsUrlDelimiter = PARTS_DELIMITER + RESTConfig.POSTS_URL + PARTS_DELIMITER;
		this.documentsUrlDelimiter = PARTS_DELIMITER + RESTConfig.DOCUMENTS_SUB_PATH + PARTS_DELIMITER;
	}	

	/** Creates a URL which points to the given user. 
	 * 
	 * @param name - the name of the user.
	 * @return A URL which points to the given user.
	 */
	public String createHrefForUser(final String name) {
		return this.userUrlPrefix + name;
	}
	
	/** Creates a URL which points to the given tag.
	 * 
	 * @param tag - the name of the tag.
	 * @return A URL which points to the given tag.
	 */
	public String createHrefForTag(final String tag) {
		return this.tagUrlPrefix + tag;
	}	

	/** Creates a URL which points to the given group.
	 * 
	 * @param name - the name of the group.
	 * @return A URL which points to the given group. 
	 */
	public String createHrefForGroup(final String name) {
		return this.groupUrlPrefix + name;
	}

	/** Creates a URL which points to the given resource.
	 * 
	 * @param userName - the name of the user which owns the resource.
	 * @param intraHash - the intra hash of the resource.
	 * @return A URL which points to the given resource.
	 */
	public String createHrefForResource(final String userName, final String intraHash) {
		return this.userUrlPrefix + userName + this.postsUrlDelimiter + intraHash;
	}
	
	/** Creates a URL which points to the given document attached to the given resource.
	 * 
	 * @param userName - the name of the user which owns the resource (and document).
	 * @param intraHash - the intra has of the resource.
	 * @param documentFileName - the name of the document.
	 * @return A URL which points to the given document.
	 */
	public String createHrefForResourceDocument(final String userName, final String intraHash, final String documentFileName) {
		return this.createHrefForResource(userName, intraHash) + this.documentsUrlDelimiter + documentFileName;
	}

	/**
	 * 
	 * @return The API URL currently used to render URLs.
	 */
	public String getApiUrl() {
		return this.apiUrl;
	}
}
