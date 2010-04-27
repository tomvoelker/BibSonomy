/**
 *  
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.rest.validation.ModelValidator;

/**
 * Some Properties for the REST Webservice.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class RestProperties {
	private static final Log log = LogFactory.getLog(RestProperties.class);
	private static RestProperties singleton = null;
	
	private ModelValidator validator = null; 
	private final Properties properties;
	private final Context jndiCtx;
	
	public static enum Property {
		CONFIGFILE("RestConfig.cfg"),
		API_URL("http://www.bibsonomy.org/api/"),   // FIXME: should be configurable
		SYSTEM_NAME("BibSonomy"),					// FIXME: should be configurable
		CONTENT_TYPE("text/xml"),
		PDF_TYPE("application/pdf"),
		API_USER_AGENT("BibSonomyWebServiceClient"),
		URL_TAGS("tags"),
		URL_CONCEPTS("concepts"),
		URL_USERS("users"),
		URL_GROUPS("groups"),
		URL_POSTS("posts"),
		URL_ADDED_POSTS("added"),
		URL_POPULAR_POSTS("popular"),
		URL_STANDARD_POSTS("standard"),
		URL_REFERENCES("references"),
		URL_DOCUMENTS("documents"),
		URL_DATE_FORMAT("yyyy-MM-dd'T'HH:mm:ss.SSS"),
		VALIDATE_XML_INPUT("false"),
		VALIDATE_XML_OUTPUT("false");

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
		} catch (final NamingException ex) {
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
			} catch (final Throwable ex) {
				log.warn("unable to initialize jndi context");
				log.debug(ex.getMessage(), ex);
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
					log.info(logMsgBuilder.append(" -> nowhere (not found)").toString());
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
	
	public String getConceptUrl() {
		return this.get(Property.URL_CONCEPTS);
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
	
	public String getDocumentsUrl() {
		return this.get(Property.URL_DOCUMENTS);
	}

	public String getAddedPostsUrl() {
		return this.get(Property.URL_ADDED_POSTS);
	}

	public String getPopularPostsUrl() {
		return this.get(Property.URL_POPULAR_POSTS);
	}
	
	public String getStandardPostsUrl() {
		return this.get(Property.URL_STANDARD_POSTS);
	}
	
	public String getReferencesUrl() {
		return this.get(Property.URL_REFERENCES);
	}
	
	public String getSystemName() {
		return this.get(Property.SYSTEM_NAME);
	}
	
	public String getPdfType(){
		return this.get(Property.PDF_TYPE);
	}

	public ModelValidator geModelValidator() {
		return this.validator;
	}

	public void setValidator(final ModelValidator validator) {
		this.validator = validator;
	}
}