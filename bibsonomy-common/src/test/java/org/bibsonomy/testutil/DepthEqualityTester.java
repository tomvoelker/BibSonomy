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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.util.ExceptionUtils;

/**
 * @author Jens Illig
 * @version $Id$
 */
public final class DepthEqualityTester  {

	private static final Log log = LogFactory.getLog(DepthEqualityTester.class);

	public static interface EqualityChecker {
		public boolean checkEquals(Object should, Object is, String path);
		public boolean checkTrue(boolean value, String path, String checkName);
	}
	
	private static final EqualityChecker simpleChecker = new EqualityChecker() {

		public boolean checkEquals(Object should, Object is, String path) {
			/*
			 * to allow null values, we first compare memory addresses
			 */
			return should == is || should.equals(is);
		}

		public boolean checkTrue(boolean value, String path, String checkName) {
			return value;
		}
		
	};

	/**
	 * Don't create instances of this class - use the static methods instead.
	 */
	private DepthEqualityTester() {
	}

	private static Set<String> toSet(final String[] excludeProperties) {
		final Set<String> skip;
		if ((excludeProperties != null) && (excludeProperties.length > 0)) {
			skip = new HashSet<String>();
			skip.addAll(Arrays.asList(excludeProperties));
		} else {
			skip = null;
		}
		return skip;
	}

	public static boolean areEqual(Object should, Object is, final EqualityChecker checker, final int maxDepth, final Pattern exclusionPattern, final String... excludeProperties) {
		return areEqual(should, is, checker, maxDepth, exclusionPattern, toSet(excludeProperties));
	}

	public static boolean areEqual(Object should, Object is, final EqualityChecker checker, final int maxDepth, final Pattern exclusionPattern, final Set<String> excludeProperties) {
		return assertPropertyEquality(should, is, checker, maxDepth, exclusionPattern, excludeProperties, "", new HashSet<Object>());
	}

	private static boolean assertPropertyEquality(final Object should, final Object is, final EqualityChecker checker, final int remainingDepth, final Pattern exclusionPattern, final Set<String> excludeProperties, final String path, final Set<Object> visited) {
		if (remainingDepth < 0) {
			return true;
		}
		if (((excludeProperties != null) && (excludeProperties.contains(path) == true)) || ((exclusionPattern != null) && (exclusionPattern.matcher(path).find() == true))) {
			log.debug("skipping '" + path + "'");
			return true;
		}
		log.debug("comparing " + path);
		if ((is == null) || (should == null)) {
			return checker.checkEquals(should, is, path);
		}
		final Class<?> shouldType = should.getClass();
		/*if (checker.checkTrue(shouldType.isAssignableFrom(is.getClass()), path, "should be " + shouldType.getName()) == false) {
			return false;
		}*/

		if ((shouldType == String.class) || (shouldType.isPrimitive() == true) || (Number.class.isAssignableFrom(shouldType) == true) || (shouldType == Date.class) || (shouldType == URL.class)) {
			return checker.checkEquals(should, is, path);
		} 
		if (remainingDepth <= 0) {
			return true;
		}
		if (visited.contains(should) == true) {
			return true;
		}
		visited.add(should);

		if ((Set.class.isAssignableFrom(shouldType) == true) && (SortedSet.class.isAssignableFrom(shouldType) == false)) {
			final Set<?> shouldSet = (Set<?>) should;
			final Set<?> isSet = (Set<?>) is;
			int i = 0;
			for (Object shouldEntry : shouldSet) {
				final String entryPath = path + "[" + i + "]";
				boolean found = false;
				for (Object isEntry : isSet) {
					if (assertPropertyEquality(shouldEntry, isEntry, simpleChecker, remainingDepth - 1, exclusionPattern, excludeProperties, entryPath, visited) == true) {
						found = true;
						break;
					}
				}
				if (checker.checkTrue(found, entryPath, "should be present") == false) {
					return false;
				}
				i++;
			}
			if (checker.checkEquals(i, isSet.size(), path + ": too much entries") == false) {
				return false;
			}
		} else if (Iterable.class.isAssignableFrom(shouldType) == true) {
			final Iterable<?> shouldIterable = (Iterable<?>) should;
			final Iterator<?> isIterator = ((Iterable<?>) is).iterator();
			int i = 0;
			for (Object shouldEntry : shouldIterable) {
				final String entryPath = path + "[" + i + "]";
				if (checker.checkTrue(isIterator.hasNext(), entryPath, "should be present") == false) {
					return false;
				}
				if (assertPropertyEquality(shouldEntry, isIterator.next(), checker, remainingDepth - 1, exclusionPattern, excludeProperties, entryPath, visited) == false) {
					return false;
				}
				i++;
			}
			if (checker.checkTrue(isIterator.hasNext() == false, path, "should not be present") == false) {
				return false;
			}
		} else {
			try {
				final BeanInfo bi = Introspector.getBeanInfo(should.getClass());
				log.debug("introspecting class " + should.getClass().getName());
				log.debug("comparing with class " + is.getClass().getName());
				for (final PropertyDescriptor d : bi.getPropertyDescriptors()) {
					final String propertyPath = (path.length() > 0) ? (path + "." + d.getName()) : d.getName();
					Exception catched = null;
					try {
						if ("class".equals(d.getName()) == false) {
							final Method getter = d.getReadMethod();
							if (getter != null) {
								if (assertPropertyEquality(getter.invoke(should, (Object[]) null), getter.invoke(is, (Object[]) null), checker, remainingDepth - 1, exclusionPattern, excludeProperties, propertyPath, visited) == false) {
									return false;
								}
							}
						}
					} catch (final IllegalArgumentException ex) {
						catched = ex;
					} catch (final IllegalAccessException ex) {
						catched = ex;
					} catch (final InvocationTargetException ex) {
						catched = ex;
					}
					if (catched != null) {
						ExceptionUtils.logErrorAndThrowRuntimeException(log, catched, "could not invoke getter of property '" + propertyPath + "'");
					}
				}
			} catch (final IntrospectionException ex) {
				ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "could not introspect object of class '" + should.getClass().getName() + "'");
			}
		}
		return true;
	}
}