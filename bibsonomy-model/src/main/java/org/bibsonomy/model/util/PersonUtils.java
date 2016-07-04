package org.bibsonomy.model.util;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.Person;
import org.bibsonomy.util.StringUtils;

/**
 * util methods for {@link Person}
 *
 * @author dzo
 */
public final class PersonUtils {
	private PersonUtils() {}
	
	/**
	 * generates the base of person identifier
	 * @param person
	 * @return the base of the person identifier
	 */
	public static String generatePersonIdBase(final Person person) {
		final String firstName = person.getMainName().getFirstName();
		final String lastName  = person.getMainName().getLastName();
		
		if (!present(lastName)) {
			throw new IllegalArgumentException("lastName may not be empty");
		}
		
		final StringBuilder sb = new StringBuilder();
		if (present(firstName)) {
			sb.append(normName(firstName).charAt(0));
			sb.append('.');
		}
		sb.append(normName(lastName));
	
		return sb.toString();
	}

	/**
	 * @param name
	 * @return
	 */
	private static String normName(final String name) {
		return StringUtils.foldToASCII(name.trim().toLowerCase().replaceAll("\\s", "_"));
	}
}
