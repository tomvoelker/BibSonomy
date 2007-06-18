package org.bibsonomy.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * Some Properties for the REST Webservice.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
@SuppressWarnings("serial")
public class RestProperties {
	private static final Logger log = Logger.getLogger(RestProperties.class);
	private static RestProperties singleton = null;
	
	private final Properties properties;
	private final Context jndiCtx;
	
	public static enum Property {
		CONFIGFILE("RestConfig.cfg"),
		API_URL("http://localhost:8080/restTomcat/api/"),
		CONTENT_TYPE("text/xml"),
		API_USER_AGENT("BibsonomyWebServiceClient"),
		URL_TAGS("tags"),
		URL_USERS("users"),
		URL_GROUPS("groups"),
		URL_POSTS("posts"),
		URL_ADDED_POSTS("added"),
		URL_POPULAR_POSTS("popular");

		private final String defaultValue;
		private Property(final String defaultValue) {
			this.defaultValue = defaultValue;
		}
	}
	
	private RestProperties(final Properties properties, final Context jndiCtx) {
		this.properties = properties;
		this.jndiCtx = jndiCtx;
	}
	
	private static String getByJndi(final String name, final Context ctx) {
		if (ctx == null) {
			return null;
		}
		try {
			return (String) ctx.lookup(name);
		} catch (NamingException ex) {
			if (log.isDebugEnabled() == true) {
				log.debug("cannot retrieve java:/comp/env/" + name);
			}
			return null;
		}
	}

	private static String get(final Property prop, final Properties properties, final Context ctx) {
		String rVal = getByJndi(prop.name(), ctx);
		if (rVal == null) {
			if (properties != null) {
				rVal = properties.getProperty(prop.name());
			}
			if (rVal == null) {
				rVal = prop.defaultValue;
			}
		}
		return rVal;
	}
	
	public String get(final Property prop) {
		return get(prop, this.properties, this.jndiCtx);
	}

	public static RestProperties getInstance() {
		if (singleton == null) {
			Context ctx;
			try {
				ctx = ((Context) new InitialContext().lookup("java:/comp/env"));
			} catch (NamingException ex) {
				log.error("unable to initialize jndi context", ex);
				ctx = null;
			}
			final String cfgFileName = get(Property.CONFIGFILE, null, ctx);
			final Properties prop = new Properties();
			try {
				final InputStream is;
				final StringBuilder logMsgBuilder = new StringBuilder("reading config file '").append(cfgFileName).append("' from ");
				final File f = new File(cfgFileName);
				if (f.exists() == true) {
					is = new FileInputStream(f);
					logMsgBuilder.append("filesystem");
				} else {
					logMsgBuilder.append("classloader");
					is = RestProperties.class.getClassLoader().getResourceAsStream(cfgFileName);
				}
				if (is != null) {
					log.info(logMsgBuilder.toString());
					prop.load(is);
				} else {
					log.info(logMsgBuilder.append("nowhere").toString());
				}
			} catch (final IOException e) {
				log.error(e.getMessage(),e);
			}
			singleton = new RestProperties(prop, ctx);
		}
		return singleton;
	}

	public String getApiUrl() {
		return this.get(Property.API_URL);
	}

	public String getContentType() {
		return this.get(Property.CONTENT_TYPE);
	}

	public String getApiUserAgent() {
		return this.get(Property.API_USER_AGENT);
	}

	public String getTagsUrl() {
		return this.get(Property.URL_TAGS);
	}

	public String getUsersUrl() {
		return this.get(Property.URL_USERS);
	}

	public String getGroupsUrl() {
		return this.get(Property.URL_GROUPS);
	}

	public String getPostsUrl() {
		return this.get(Property.URL_POSTS);
	}

	public String getAddedPostsUrl() {
		return this.get(Property.URL_ADDED_POSTS);
	}

	public String getPopularPostsUrl() {
		return this.get(Property.URL_POPULAR_POSTS);
	}
}