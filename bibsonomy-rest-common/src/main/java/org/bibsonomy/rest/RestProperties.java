package org.bibsonomy.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Some Properties for the REST Webservice.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
@SuppressWarnings("serial")
public class RestProperties extends Properties {

	private static RestProperties properties = null;

	/* key names */
	private static final String PROPERTY_API_URL = "RestApiURL";
	private static final String PROPERTY_CONTENT_TYPE = "DefaultContentType";
	private static final String PROPERTY_API_USER_AGENT = "UserAgentOfAPI";
	private static final String PROPERTY_URL_TAGS = "TagsURL";
	private static final String PROPERTY_URL_USERS = "UsersURL";
	private static final String PROPERTY_URL_GROUPS = "GroupsURL";
	private static final String PROPERTY_URL_POSTS = "PostsURL";
	private static final String PROPERTY_URL_ADDED_POSTS = "AddedPostsURL";
	private static final String PROPERTY_URL_POPULAR_POSTS = "PopularPostsURL";

	/* default values */
	private static final String DEFAULT_API_URL = "http://localhost:8080/restTomcat/api/";
	private static final String DEFAULT_CONTENT_TYPE = "text/xml";
	private static final String DEFAULT_API_USER_AGENT = "BibsonomyWebServiceClient";
	private static final String DEFAULT_URL_TAGS = "tags";
	private static final String DEFAULT_URL_USERS = "users";
	private static final String DEFAULT_URL_GROUPS = "groups";
	private static final String DEFAULT_URL_POSTS = "posts";
	private static final String DEFAULT_URL_ADDED_POSTS = "added";
	private static final String DEFAULT_URL_POPULAR_POSTS = "popular";

	/* some internals */
	private static final String CONFIGFILE = "RestConfig.cfg";

	private RestProperties() {
		super();
	}

	private RestProperties(final Properties properties) {
		super(properties);
		// store();
	}

	public static RestProperties getInstance() {
		if (properties == null) {
			final Properties prop = new Properties();
			try {
				final File f = new File(CONFIGFILE);
				if (f.exists()) {
					prop.load(new FileInputStream(f));
				} else {
					// System.err.println( "RestProperties.getInstance()" );
					// System.err.println( "could not find config file." );
					// System.exit( -1 );
					// f.createNewFile();
				}
			} catch (final FileNotFoundException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			properties = new RestProperties(prop);
		}
		return properties;
	}

	public void store() {
		try {
			super.store(new FileOutputStream(new File(CONFIGFILE)), "");
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public String getApiUrl() {
		if (getProperty(PROPERTY_API_URL) != null) {
			return getProperty(PROPERTY_API_URL).trim();
		} else {
			return DEFAULT_API_URL;
		}
	}

	public String getContentType() {
		if (getProperty(PROPERTY_CONTENT_TYPE) != null) {
			return getProperty(PROPERTY_CONTENT_TYPE).trim();
		} else {
			return DEFAULT_CONTENT_TYPE;
		}
	}

	public String getApiUserAgent() {
		if (getProperty(PROPERTY_API_USER_AGENT) != null) {
			return getProperty(PROPERTY_API_USER_AGENT).trim();
		} else {
			return DEFAULT_API_USER_AGENT;
		}
	}

	public String getTagsUrl() {
		if (getProperty(PROPERTY_URL_TAGS) != null) {
			return getProperty(PROPERTY_URL_TAGS).trim();
		} else {
			return DEFAULT_URL_TAGS;
		}
	}

	public String getUsersUrl() {
		if (getProperty(PROPERTY_URL_USERS) != null) {
			return getProperty(PROPERTY_URL_USERS).trim();
		} else {
			return DEFAULT_URL_USERS;
		}
	}

	public String getGroupsUrl() {
		if (getProperty(PROPERTY_URL_GROUPS) != null) {
			return getProperty(PROPERTY_URL_GROUPS).trim();
		} else {
			return DEFAULT_URL_GROUPS;
		}
	}

	public String getPostsUrl() {
		if (getProperty(PROPERTY_URL_POSTS) != null) {
			return getProperty(PROPERTY_URL_POSTS).trim();
		} else {
			return DEFAULT_URL_POSTS;
		}
	}

	public String getAddedPostsUrl() {
		if (getProperty(PROPERTY_URL_ADDED_POSTS) != null) {
			return getProperty(PROPERTY_URL_ADDED_POSTS).trim();
		} else {
			return DEFAULT_URL_ADDED_POSTS;
		}
	}

	public String getPopularPostsUrl() {
		if (getProperty(PROPERTY_URL_POPULAR_POSTS) != null) {
			return getProperty(PROPERTY_URL_POPULAR_POSTS).trim();
		} else {
			return DEFAULT_URL_POPULAR_POSTS;
		}
	}
}