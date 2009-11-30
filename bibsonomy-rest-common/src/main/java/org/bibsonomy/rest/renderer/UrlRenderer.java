/**
 *  
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

import static org.bibsonomy.rest.RestProperties.Property.API_URL;
import static org.bibsonomy.rest.RestProperties.Property.URL_GROUPS;
import static org.bibsonomy.rest.RestProperties.Property.URL_POSTS;
import static org.bibsonomy.rest.RestProperties.Property.URL_TAGS;
import static org.bibsonomy.rest.RestProperties.Property.URL_USERS;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.RestProperties.Property;

/** 
 * This renderer creates URLs according to BibSonomys REST URL scheme.
 * 
 * @author rja
 * @version $Id$
 */
public class UrlRenderer {
	private static final String SLASH = "/";
	
	private final String userUrlPrefix;
	private final String groupUrlPrefix;
	private final String tagUrlPrefix;

	private final String partsDelimiter = SLASH;
	private final String postsUrlDelimiter;
	private final String documentsUrlDelimiter;
	
	private final DateFormat dateFormat;
	
	private static UrlRenderer urlRenderer;
	
	private UrlRenderer() {
		final RestProperties properties = RestProperties.getInstance();
		final String apiUrl = properties.get(API_URL);
		this.userUrlPrefix = apiUrl + properties.get(URL_USERS) + partsDelimiter;
		this.groupUrlPrefix = apiUrl + properties.get(URL_GROUPS) + partsDelimiter;
		this.tagUrlPrefix = apiUrl + properties.get(URL_TAGS) + partsDelimiter;
		this.postsUrlDelimiter = partsDelimiter + properties.get(URL_POSTS) + partsDelimiter;
		this.documentsUrlDelimiter = partsDelimiter + properties.get(Property.URL_DOCUMENTS) + partsDelimiter;
		this.dateFormat = new SimpleDateFormat(properties.get(Property.URL_DATE_FORMAT));
	}

	/**
	 * @return An instance of {@link UrlRenderer}.
	 */
	public static UrlRenderer getInstance() {
		if (urlRenderer == null) {
			urlRenderer = new UrlRenderer();
		}
		return urlRenderer;
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
	
	/** Creates a URL which points to the given resource.
	 * 
	 * @param userName - the name of the user which owns the resource.
	 * @param intraHash - the intra hash of the resource.
	 * @param date - the date at which the resource has been posted.
	 * @return A URL which points to the given resource.
	 */
	public String createHrefForResource(final String userName, final String intraHash, final Date date) {
		return this.userUrlPrefix + userName + this.postsUrlDelimiter + intraHash + this.partsDelimiter + dateFormat.format(date);
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
}
