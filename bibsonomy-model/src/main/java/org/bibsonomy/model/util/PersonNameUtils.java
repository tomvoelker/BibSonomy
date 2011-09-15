/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

package org.bibsonomy.model.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;

/**
 * Nice place for static util methods regarding names of persons.
 *
 * @author  Jens Illig
 * @version $Id$
 */
public class PersonNameUtils {

	/**
	 * the delimiter used for separating person names
	 */
	public static final String PERSON_NAME_DELIMITER = " and ";

	/**
	 * By default, all author and editor names are in "Last, First" order
	 */
	public static final boolean DEFAULT_LAST_FIRST_NAMES = true;


	/**
	 * Analyses a string of names of the form "J. T. Kirk and M. Scott".
	 * 
	 * @param persons the source string 
	 * @return the result
	 * @throws PersonListParserException 
	 */
	public static List<PersonName> discoverPersonNames(final String persons) throws PersonListParserException {
		return PersonNameParser.parse(persons);
	}
	
	/**
	 * Like {@link #discoverPersonNames(String)} but ignores exceptions and 
	 * instead returns null.
	 * 
	 * @param persons
	 * @return the parsed person name list or null
	 */
	public static List<PersonName> discoverPersonNamesIgnoreExceptions(final String persons) {
		try {
			return PersonNameParser.parse(persons);
		} catch (PersonListParserException ex) {
			return null;
		}
	}

	/**
	 * Converts a name in the format "Last, First" into the "First Last" format
	 * by splitting it at the first comma.
	 * If the name is already in that format (=no comma found), the name is returned as is.
	 * 
	 * @param name
	 * @return The name in format "First Last"
	 */
	public static String lastFirstToFirstLast(final String name) {
		if (present(name)) {
			final int indexOf = name.indexOf(PersonName.LAST_FIRST_DELIMITER);
			if (indexOf >= 0) {
				return name.substring(indexOf + 1).trim() + " " + name.substring(0, indexOf).trim();
			}
		}
		return name;
	}

	/**
	 * @param persons
	 * @return The first person's last name.
	 */
	public static String getFirstPersonsLastName(final List<PersonName> persons) {
		if (present(persons)) {
			return persons.get(0).getLastName();
		}
		return null;
	}

	/**
	 * @see PersonNameUtils#serializePersonNames(List, boolean, String)
	 * 
	 * @param personNames
	 * @return The joined names or <code>null</code> if the list is empty.
	 */
	public static String serializePersonNames(final List<PersonName> personNames) {
		return serializePersonNames(personNames, DEFAULT_LAST_FIRST_NAMES);
	}

	/**
	 * @see PersonNameUtils#serializePersonNames(List, boolean, String)
	 * 
	 * @param personNames
	 * @param delimiter 
	 * @return The joined names or <code>null</code> if the list is empty.
	 */
	public static String serializePersonNames(final List<PersonName> personNames, final String delimiter) {
		return serializePersonNames(personNames, DEFAULT_LAST_FIRST_NAMES, delimiter);
	}

	/**
	 * Joins the names of the persons in "Last, First" form (if lastFirstNames is
	 * <code>true</code>) or "First Last" form (if lastFirstNames is
	 * <code>false</code>) using the {@link #PERSON_NAME_DELIMITER}.
	 * 
	 * @param personNames
	 * @param lastFirstNames
	 * @return The joined names or <code>null</code> if the list is empty.
	 */
	public static String serializePersonNames(final List<PersonName> personNames, final boolean lastFirstNames) {
		return serializePersonNames(personNames, lastFirstNames, PERSON_NAME_DELIMITER);
	}

	/**
	 * Joins the names of the persons in "Last, First" form (if lastFirstNames is
	 * <code>true</code>) or "First Last" form (if lastFirstNames is
	 * <code>false</code>) using the given delimiter
	 * 
	 * @param personNames
	 * @param lastFirstNames
	 * @param delimiter - a string used as delimiter between person names.
	 * @return The joined names or <code>null</code> if the list is empty.
	 */
	public static String serializePersonNames(final List<PersonName> personNames, final boolean lastFirstNames, final String delimiter) {
		if (!present(personNames)) return null;
		final StringBuilder sb = new StringBuilder();
		int i = personNames.size();
		for (final PersonName personName : personNames) {
			i--;
			sb.append(serializePersonName(personName, lastFirstNames));
			if (i > 0) {
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}

	/**
	 * @param personName
	 * @return The name or <code>null</code> if the name is empty.
	 */
	public static String serializePersonName(final PersonName personName) {
		return serializePersonName(personName, DEFAULT_LAST_FIRST_NAMES);
	}

	/**
	 * Returns the name of the person in "Last, First" form (if lastFirstNames is
	 * <code>true</code>) or "First Last" form (if lastFirstNames is
	 * <code>false</code>)
	 * 
	 * @param personName
	 * @param lastFirstName
	 * @return The name or <code>null</code> if the name is empty.
	 */
	public static String serializePersonName(final PersonName personName, final boolean lastFirstName) {
		if (!present(personName)) return null;
		final String first;
		final String last;
		final String delim;
		if (lastFirstName) {
			first = personName.getLastName();
			last = personName.getFirstName();
			delim = PersonName.LAST_FIRST_DELIMITER + " ";
		} else {
			first = personName.getFirstName();
			last = personName.getLastName();
			delim = " ";
		}
		if (present(first)) {
			if (present(last)) {
				return first + delim + last;
			}
			return first;
		} 
		if (present(last)) {
			return last;
		}
		return null;
	}

}