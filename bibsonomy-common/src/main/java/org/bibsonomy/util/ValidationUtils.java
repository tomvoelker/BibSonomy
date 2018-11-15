/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 * <p>
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 * University of Kassel, Germany
 * http://www.kde.cs.uni-kassel.de/
 * Data Mining and Information Retrieval Group,
 * University of Würzburg, Germany
 * http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 * L3S Research Center,
 * Leibniz University Hannover, Germany
 * http://www.l3s.de/
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.util;

import org.bibsonomy.common.enums.GroupID;

import java.util.Collection;
import java.util.Map;

/**
 * @author Jens Illig
 */
public class ValidationUtils {

	/**
	 * Test whether the given subject is present.
	 * @param subject the object to test
	 * @param message the message to throw in case of non-existence
	 * @param <T> type of subject
	 * @return the subject if present, else throws an IllegalArgumentException
	 */
	public static <T> T requirePresent(final T subject, final String message) {
		if (subject instanceof String && !present((String) subject)) {
			throw new IllegalArgumentException(message);
		}
		if (subject instanceof CharSequence && !present((CharSequence) subject)) {
			throw new IllegalArgumentException(message);
		}
		if (subject instanceof Collection && !present((Collection<?>) subject)) {
			throw new IllegalArgumentException(message);
		}
		if (subject instanceof Map && !present((Map<?, ?>) subject)) {
			throw new IllegalArgumentException(message);
		}
		if (subject instanceof GroupID && !present((GroupID) subject)) {
			throw new IllegalArgumentException(message);
		}
		if (subject instanceof Object[] && !present((Object[]) subject)) {
			throw new IllegalArgumentException(message);
		}
		if (!present(subject)) {
			throw new IllegalArgumentException(message);
		}
		return subject;
	}

	/**
	 * @param string argument to check
	 * @return false iff the argument is null or has zero trimmed length
	 */
	public static boolean present(final String string) {
		return ((string != null) && (string.trim().length() > 0));
	}

	/**
	 * @param charSequence argument to check
	 * @return false iff the argument is null or has zero trimmed length
	 */
	public static boolean present(final CharSequence charSequence) {
		return ((charSequence != null) && (charSequence.length() > 0));
	}


	/**
	 * @param collection argument to check
	 * @return false iff the argument is null or has zero size
	 */
	public static boolean present(final Collection<?> collection) {
		return ((collection != null) && (collection.size() > 0));
	}

	/**
	 * @param map argument to check
	 * @return false iff the argument is null or has zero size
	 */
	public static boolean present(final Map<?, ?> map) {
		return ((map != null) && (map.size() > 0));
	}

	/**
	 * @param object argument to check
	 * @return false iff the argument is null
	 */
	public static boolean present(final Object object) {
		return (object != null);
	}

	/**
	 * @param objects array to check
	 * @return false iff the argument is null or has zero length
	 */
	public static boolean present(final Object[] objects) {
		return (objects != null) && objects.length > 0;
	}

	/**
	 * @param gid argument to check
	 * @return false iff the argument is null or has invalid value
	 */
	public static boolean present(final GroupID gid) {
		return ((gid != null) && (gid != GroupID.INVALID));
	}

	/**
	 * @param gid argument to check
	 * @return false iff the argument is null or has invalid value
	 */
	public static boolean presentValidGroupId(final int gid) {
		return (gid != GroupID.INVALID.getId());
	}

	/**
	 * @param requested argument to check
	 * @param supported reference argument for comparison
	 * @return true if <code>requested</code> is null or equals to one of the following arguments
	 */
	public static boolean nullOrEqual(final Object requested, final Object... supported) {
		if (requested == null) {
			return true;
		}
		for (final Object support : supported) {
			if (requested == support) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param collection
	 * @param entry
	 * @return <code>true</code> if the collection contains the entry
	 */
	public static <E> boolean safeContains(final Collection<E> collection, final E entry) {
		return present(collection) && collection.contains(entry);
	}

	/**
	 * @param obj object to be tested
	 * @throws IllegalStateException if given object reference is null
	 */
	public static void assertNotNull(Object obj) {
		if (obj == null) {
			throw new IllegalStateException("should not be null");
		}
	}

	/**
	 * equals including null values
	 * @param a
	 * @param b
	 * @return <code>true</code> if both objects are null or equal, <code>false</code> otherwise
	 */
	public static boolean equalsWithNull(Object a, Object b) {
		if (a == b) {
			return true;
		}
		if ((a == null) || (b == null)) {
			return false;
		}
		return a.equals(b);
	}

}