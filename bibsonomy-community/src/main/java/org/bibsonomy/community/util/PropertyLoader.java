package org.bibsonomy.community.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertyLoader {
	private final static Log log = LogFactory.getLog(PropertyLoader.class);
	
	public static Properties openPropertyFile(String fileName) throws IOException {
		final Properties props = new Properties();
		// read properties
		try {
			props.load(new FileInputStream(new File(fileName)));
			log.debug("Loading configuration from file system.");
		} catch( IOException ex ) {
			props.load(JNDITestDatabaseBinder.class.getClassLoader().getResourceAsStream(fileName));		
			log.debug("Loading configuration from class path.");
		}
		return props;
	}	
}
