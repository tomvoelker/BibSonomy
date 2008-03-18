package org.bibsonomy.util;

import org.bibsonomy.common.exceptions.InternServerException;
import static org.bibsonomy.util.ValidationUtils.present;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class EnumUtils {

	/**
	 * Searches for an enum contained in <code>values</code> where its
	 * lowercase name matches the string <code>name</code>.
	 * 
	 * @param <T>
	 *            an enum
	 * @param values
	 *            the values of an enum
	 * @param name
	 *            the name of an enum
	 * @return an enum contained in values or null
	 */
	public static <T extends Enum<?>> T searchEnumByName(final T[] values, final String name) {
		if (present(name) == false) throw new InternServerException("Parameter name must be set");
		for (final T value : values) {
			if (value.name().toLowerCase().equals(name.trim().toLowerCase())) return value;
		}
		return null;
	}
}