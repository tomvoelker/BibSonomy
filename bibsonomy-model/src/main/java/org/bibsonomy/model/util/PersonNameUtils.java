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

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.util.StringUtils;

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
	 * this one is used to extract person names where is is allowed that there
	 * are several "and" delimiters in a row, e.g., "D.E. Knuth and and Foo Bar". 
	 */
	private static final String PERSON_NAME_DELIMITER_EXTRACTOR = "\\s+" + PERSON_NAME_DELIMITER.trim() + "\\s*?";

	/**
	 * By default, all author and editor names are in "Last, First" order
	 */
	public static final boolean DEFAULT_LAST_FIRST_NAMES = true;


	/**
	 * Analyses a string of names of the form "J. T. Kirk and M. Scott"
	 * 
	 * Currently can't handle the case where the persons strings starts with an
	 * "and "
	 * 
	 * @param persons the source string 
	 * @return the result
	 */
	public static List<PersonName> discoverPersonNames(final String persons) {
		final List<PersonName> authors = new LinkedList<PersonName>();
		if (present(persons)) {
			for (final String token : persons.split(PERSON_NAME_DELIMITER_EXTRACTOR)) {
				if (present(token)) authors.add(discoverPersonName(token));
			}
		}
		return authors;
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
	 * Given a list of person names separated by {@link #PERSON_NAME_DELIMITER}, we check
	 * if one of them is in "Last, First" format and use {@link #lastFirstToFirstLast(String)}
	 * to transform them to "First Last.
	 * 
	 * The string is only changed if it contains at least one comma.
	 * 
	 * @param names
	 * @return a list of person names, where each name is in the "First Last" format. 
	 */
	public static String lastFirstToFirstLastMany(final String names) {
		if (present(names)) {
			if (names.contains(PersonName.LAST_FIRST_DELIMITER)) {
				final StringBuilder namesNew = new StringBuilder();

				final String[] split = names.split(PERSON_NAME_DELIMITER_EXTRACTOR);
				for (int i = 0; i < split.length; i++) {
					final String name = split[i].trim();
					if (present(name)) {
						namesNew.append(lastFirstToFirstLast(name));
						if (i < split.length - 1) namesNew.append(PERSON_NAME_DELIMITER);
					}
				}
				return namesNew.toString();
			}
		}
		return names;
	}

	/**
	 * Tries to detect the first name and last name of the given name.
	 * 
	 * @param name
	 * @return The extracted person's name
	 */
	public static PersonName discoverPersonName(final String name) {
		final PersonName personName = new PersonName();
		if (present(name)) {
			/*
			 * DBLP author names sometimes contain numbers (when there are
			 * several authors with the same name. Here we remove those numbers
			 */
			final String cleanedName = StringUtils.removeSingleNumbers(name).trim();
			/*
			 * Names can be in several formats:
			 * 
			 * 1) First (preLast) Last
			 * 2) (preLast) Last, First
			 * 3) {Long name of a Company}
			 * 4) First {Last, Jr.} (TODO: we can't handle this case)
			 * 5) Last, Jr., First (TODO: we can't handle this case)
			 * 
			 * If the name starts with a brace and ends with a brace, we assume case 3).
			 */
			final int indexOfLbr = cleanedName.indexOf("{");
			final int indexOfRbr = cleanedName.lastIndexOf("}");
			if (indexOfLbr == 0 && indexOfRbr == cleanedName.length() - 1) {
				/*
				 * 3) {Long name of Company}
				 * 
				 * We do not remove the braces and use the complete "name" as last name. 
				 */
				personName.setLastName(cleanedName);
				return personName;
			}
			/*
			 * If the name contains a comma, we assume case 2).
			 */
			final int indexOfComma = cleanedName.indexOf(PersonName.LAST_FIRST_DELIMITER);
			if (indexOfComma >= 0) {
				/*
				 * 2) We assume (preLast) Last, First.
				 * Since our PersonName does not have an extra "preLast" attribute,
				 * we store it together with "Last".
				 */
				personName.setFirstName(cleanedName.substring(indexOfComma + 1).trim());
				personName.setLastName(cleanedName.substring(0, indexOfComma).trim());
				return personName;
			}
			/*
			 * 1) First Last ... its not so obvious, which part is what. 
			 * 
			 * We assume that a name has either only one (abbreviated or not) 
			 * first name, or several - while all except the first must be 
			 * abbreviated. The last name then begins at the first word that 
			 * does not contain a ".".
			 * Or, the last name begins at the first word with a lower case 
			 * letter.
			 * 
			 */

			/*
			 * first: split at whitespace
			 */
			final String[] nameList = cleanedName.split("\\s+");
			/*
			 * detect first name and last name
			 */
			final StringBuilder firstNameBuilder = new StringBuilder();
			int i = 0;
			while (i < nameList.length - 1) { // iterate up to the last but one part
				final String part = nameList[i++];
				firstNameBuilder.append(part + " ");
				/*
				 * stop, if this is the last abbreviated first name
				 * or 
				 * the next part begins with a lowercase letter
				 */
				final String nextPart = nameList[i];
				if ((part.contains(".") && !nextPart.endsWith(".")) || nextPart.matches("^[a-z].*")) {
					break;
				}
			}

			final StringBuilder lastNameBuilder = new StringBuilder();
			while (i < nameList.length) {
				lastNameBuilder.append(nameList[i++] + " ");
			}

			personName.setFirstName(firstNameBuilder.toString().trim());
			personName.setLastName(lastNameBuilder.toString().trim());
		}
		return personName;
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