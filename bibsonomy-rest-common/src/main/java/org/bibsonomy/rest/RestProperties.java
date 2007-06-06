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
		return this.getPropertyOrDefault(PROPERTY_API_URL, DEFAULT_API_URL);
	}

	public String getContentType() {
		return this.getPropertyOrDefault(PROPERTY_CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
	}

	public String getApiUserAgent() {
		return this.getPropertyOrDefault(PROPERTY_API_USER_AGENT, DEFAULT_API_USER_AGENT);
	}

	public String getTagsUrl() {
		return this.getPropertyOrDefault(PROPERTY_URL_TAGS, DEFAULT_URL_TAGS);
	}

	public String getUsersUrl() {
		return this.getPropertyOrDefault(PROPERTY_URL_USERS, DEFAULT_URL_USERS);
	}

	public String getGroupsUrl() {
		return this.getPropertyOrDefault(PROPERTY_URL_GROUPS, DEFAULT_URL_GROUPS);
	}

	public String getPostsUrl() {
		return this.getPropertyOrDefault(PROPERTY_URL_POSTS, DEFAULT_URL_POSTS);
	}

	public String getAddedPostsUrl() {
		return this.getPropertyOrDefault(PROPERTY_URL_ADDED_POSTS, DEFAULT_URL_ADDED_POSTS);
	}

	public String getPopularPostsUrl() {
		return this.getPropertyOrDefault(PROPERTY_URL_POPULAR_POSTS, DEFAULT_URL_POPULAR_POSTS);
	}

	private final String getPropertyOrDefault(final String property, final String def) {
		if (getProperty(property) != null) {
			return getProperty(property).trim();
		} else {
			return def;
		}
	}
}