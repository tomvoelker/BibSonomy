/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
