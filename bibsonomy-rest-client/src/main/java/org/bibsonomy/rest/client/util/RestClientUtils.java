package org.bibsonomy.rest.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author dzo
 * @version $Id$
 */
public class RestClientUtils {
	private static final Log log = LogFactory.getLog(RestClientUtils.class);
	
	/**
	 * the content charset used by the rest client
	 */
	public static final String CONTENT_CHARSET = "UTF-8";
	
	private static final String PROPERTIES_FILE_NAME = "bibsonomy-rest-client.properties";
	private static final String PROPERTIES_VERSION_KEY = "version";
	
	private static String REST_CLIENT_VERSION = "unknown";
	
	/**
	 * @return the version of the client
	 */
	public static String getRestClientVersion() {
		return REST_CLIENT_VERSION;
	}
	
	static {		
		try {
			final Properties properties = new Properties();
			
			final InputStream stream = RestClientUtils.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
			properties.load(stream);
			stream.close();
			
			REST_CLIENT_VERSION = properties.getProperty(PROPERTIES_VERSION_KEY);
		} catch (final IOException ex) {
			log.error("could not load version", ex);
		}
	}
	
	
}
