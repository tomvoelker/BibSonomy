package org.bibsonomy.rest.renderer;

import static org.bibsonomy.rest.RestProperties.Property.API_URL;
import static org.bibsonomy.rest.RestProperties.Property.URL_GROUPS;
import static org.bibsonomy.rest.RestProperties.Property.URL_POSTS;
import static org.bibsonomy.rest.RestProperties.Property.URL_TAGS;
import static org.bibsonomy.rest.RestProperties.Property.URL_USERS;

import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.RestProperties.Property;

/** 
 * This renderer creates URLs according to BibSonomys REST URL scheme.
 * 
 * @author rja
 * @version $Id$
 */
public class UrlRenderer {
	private final String userUrlPrefix;
	private final String groupUrlPrefix;
	private final String tagUrlPrefix;
	private final String postsUrlDelimiter;
	private final String documentsUrlDelimiter;
	
	private static UrlRenderer urlRenderer;
	
	private UrlRenderer() {
		final RestProperties properties = RestProperties.getInstance();
		final String apiUrl = properties.get(API_URL);
		this.userUrlPrefix = apiUrl + properties.get(URL_USERS) + "/";
		this.groupUrlPrefix = apiUrl + properties.get(URL_GROUPS) + "/";
		this.tagUrlPrefix = apiUrl + properties.get(URL_TAGS) + "/";
		this.postsUrlDelimiter = "/" + properties.get(URL_POSTS) + "/";
		this.documentsUrlDelimiter = "/" + properties.get(Property.URL_DOCUMENTS) + "/";
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
