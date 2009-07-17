/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scraper.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * @author rja
 * @version $Id$
 */
public class ConfigUtil {
	/**
	 * Logger
	 */
	private static final Logger log = Logger.getLogger(ConfigUtil.class);

	/**
	 * path so settings.properties
	 */
	private static final String PATH_TO_SETTINGSFILE = "scraper.properties";
	

	/** Loads the configuration file for the scrapers. If no file could be found,
	 *  empty properties are returned.
	 * 
	 * @return The configuration properties.
	 */
	public static Properties loadProperties() {
		final Properties properties = new Properties();
		try {
			properties.load(ConfigUtil.class.getClassLoader().getResourceAsStream(PATH_TO_SETTINGSFILE));
		} catch (FileNotFoundException ex) {
			log.error("Could not load properties.", ex);
		} catch (IOException ex) {
			log.error("Could not load properties.", ex);
		}
		return properties;
	}
	
	/** Returns the given environment variable, or <code>null</code> if it could not be found.
	 * 
	 * @param key - the name of the environment variable.
	 * @return The value.
	 */
	public static String getEnvironmentVariable(final String key) {
		try {
			return ((String) ((Context) new InitialContext().lookup("java:/comp/env")).lookup(key));
		} catch (NamingException ex) {
			log.warn("Could not get environment variable '" + key + "'.", ex);
		}
		return null;
	}
	
}
