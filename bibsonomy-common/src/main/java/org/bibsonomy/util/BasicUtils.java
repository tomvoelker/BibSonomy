package org.bibsonomy.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * some basic utils
 *
 * @author dzo
 */
public final class BasicUtils {
	private static final Log log = LogFactory.getLog(BasicUtils.class);
	
	private BasicUtils() {}
	
	private static final String PROPERTIES_FILE_NAME = "org/bibsonomy/common/bibsonomy-common.properties";
	private static final String PROPERTIES_VERSION_KEY = "version";
	
	/** the version of the system */
	public static final String VERSION;
	
	static {
		String version = "unknown";
		/*
		 * load version of client from properties file
		 */
		try {
			final Properties properties = new Properties();
			
			final InputStream stream = BasicUtils.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
			properties.load(stream);
			stream.close();
			
			version = properties.getProperty(PROPERTIES_VERSION_KEY);
		} catch (final IOException ex) {
			log.error("could not load version", ex);
		}
		VERSION = version;
	}
}
