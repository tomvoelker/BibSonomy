/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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

package org.bibsonomy.testutil;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.mockejb.jndi.MockContextFactory;

/**
 * Loads a project.properties file and binds all properties to JNDI.
 * 
 * @author Robert Jäschke
 * @version $Id$
 */
public final class JNDITestProjectParams {

	/**
	 * Don't create instances of this class - use the static methods instead.
	 */
	private JNDITestProjectParams() {
	}

	/**
	 * Main method: read configuration file 'project.properties' and register
	 * properties via JNDI.
	 */
	public static final void bind() {
		final Context ctx;
		final Properties props = new Properties();

		try {
			/*
			 * read project properties
			 */
			props.load(JNDITestProjectParams.class.getClassLoader().getResourceAsStream("project.properties"));
			/*
			 * create Mock JNDI context
			 */
			MockContextFactory.setAsInitial();

			ctx = new InitialContext();

			/*
			 * FIXME: this seems to work, but why?
			 */
			ctx.bind("java:/comp/env", new InitialContext());

			/*
			 * copy all properties into context
			 */
			final Set<Object> keys = props.keySet();
			for (final Object o : keys) {
				final String key = (String) o;
				ctx.bind(key, props.getProperty(key));
			}

		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (NamingException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}

//		try {			
//			ctx.bind("java:comp/env/jdbc/bibsonomy", ds);
//		}
//		catch (NamingException ex) {
//			log.error("Error when trying to bind test database connection via JNDI");
//			log.error(ex.getMessage());
//		}
	}

	/**
	 * Go back to original state
	 */
	public static void unbind() {
		MockContextFactory.revertSetAsInitial();
	}
}