package org.bibsonomy.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dzo
 * @version $Id$
 */
public class Sets {
	
	/**
	 * @param elements
	 * @return a set
	 */
	public static <T> Set<T> asSet(final T... elements) {
		final Set<T> set = new HashSet<T>();
		if (present(elements)) {
			for (T t : elements) {
				set.add(t);
			}
		}
		return set;
	}
}
