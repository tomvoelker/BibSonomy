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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.testutil.DepthEqualityTester.EqualityChecker;
import org.bibsonomy.util.ExceptionUtils;

/**
 * Common things for tests.
 * 
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class CommonModelUtils {

	private static final Log log = LogFactory.getLog(CommonModelUtils.class);

	/**
	 * Don't create instances of this class - use the static methods instead.
	 */
	protected CommonModelUtils() {
	}

	/**
	 * Calls every setter on an object and fills it wiht dummy values.
	 * @param obj 
	 */
	public static void setBeanPropertiesOn(final Object obj) {
		try {
			final BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
			for (final PropertyDescriptor d : bi.getPropertyDescriptors()) {
				try {
					final Method setter = d.getWriteMethod();
					final Method getter = d.getReadMethod();
					if ((setter != null) && (getter != null)) {
						setter.invoke(obj, new Object[] { getDummyValue(d.getPropertyType(), d.getName()) });
					}
				} catch (final Exception ex) {
					ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "could not invoke setter '" + d.getName() + "'");
				}
			}
		} catch (final IntrospectionException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "could not introspect object of class '" + obj.getClass().getName() + "'");
		}
	}

	/**
	 * Returns dummy values for some primitive types and classes
	 */
	private static Object getDummyValue(final Class<?> type, final String name) {
		if (String.class == type) {
			return "test-" + name;
		}
		if ((int.class == type) || (Integer.class == type)) {
			return Math.abs(name.hashCode());
		}
		if ((boolean.class == type) || (Boolean.class == type)) {
			return (name.hashCode() % 2 == 0);
		}
		if (URL.class == type) {
			try {
				return new URL("http://www.bibsonomy.org/test/" + name);
			} catch (final MalformedURLException ex) {
				throw new RuntimeException(ex);
			}
		}
		if (Privlevel.class == type) {
			return Privlevel.MEMBERS;
		}
		log.debug("no dummy value for type '" + type.getName() + "'");
		return null;
	}

	/**
	 * Checks whether every property of two objects (should and is) match.
	 * 
	 * @param should
	 * @param is
	 * @param maxDepth
	 * @param excludePropertiesPattern
	 * @param excludeProperties
	 */
	public static void assertPropertyEquality(final Object should, final Object is, final int maxDepth, final Pattern excludePropertiesPattern, final String... excludeProperties) {
		final EqualityChecker checker = new EqualityChecker() {

			public boolean checkEquals(Object should, Object is, String path) {
				assertEquals(path, should, is);
				return true;
			}

			public boolean checkTrue(boolean value, String path, String checkName) {
				assertTrue(path + " " + checkName, value);
				return true;
			}

		};
		DepthEqualityTester.areEqual(should, is, checker, maxDepth, excludePropertiesPattern, excludeProperties);
	}

	/**
	 * Retruns a HashSet built from an array of strings that are all converted
	 * to lowercase.
	 * 
	 * @param values
	 * @return HashSet
	 */
	public static Set<String> buildLowerCaseHashSet(final String... values) {
		final Set<String> rVal = new HashSet<String>();
		for (final String value : values) {
			rVal.add(value.toLowerCase());
		}
		return rVal;
	}

	/**
	 * Convenience method for buildLowerCaseHashSet(final String... values).
	 * 
	 * @param values
	 * @return HashSet
	 */
	public static Set<String> buildLowerCaseHashSet(final Collection<String> values) {
		return buildLowerCaseHashSet(values.toArray(new String[values.size()]));
	}

}