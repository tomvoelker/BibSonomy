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

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
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
	 * By default, all author and editor names are in "Last, First" order
	 */
	public static final boolean DEFAULT_LAST_FIRST_NAMES = true;
	
	private static final Pattern SINGLE_LETTER = Pattern.compile("(\\p{L})");
	
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

	
	/**
	 * July 2010: added "orComma" since we now support the "Last, First" name format 
	 * where we need the comma in {@link #normalizePerson(PersonName)} to extract the
	 * first and the last name.
	 * 
	 * @param persons 
	 * @return The normalized persons - divided by ", " and enclosed in
	 * brackets "[ ]"l
	 */
	public static String getNormalizedPersons(final Collection<PersonName> persons) {
		if (!present(persons)) return "";
		return StringUtils.getStringFromList(normalizePersonList(persons));
	}
	

	/**
	 * Normalizes a collection of persons by normalizing their names 
	 * ({@link #normalizePerson(PersonName)}) and sorting them.
	 *  
	 * @param persons - a list of persons. 
	 * @return A sorted set of normalized persons.
	 */
	private static SortedSet<String> normalizePersonList(final Collection<PersonName> persons) {
		final SortedSet<String> normalized = new TreeSet<String>();
		for (final PersonName personName : persons) {
			normalized.add(normalizePerson(personName));
		}
		return normalized;
	}

	/**
	 * Used for "sloppy" hashes, i.e., the inter hash.
	 * <p>
	 * The person name is normalized according to the following scheme:
	 * <tt>x.last</tt>, where <tt>x</tt> is the first letter of the first name
	 * and <tt>last</tt> is the last name.
	 * </p>
	 * 
	 * Example:
	 * <pre>
	 * Donald E. Knuth       --&gt; d.knuth
	 * D.E.      Knuth       --&gt; d.knuth
	 * Donald    Knuth       --&gt; d.knuth
	 *           Knuth       --&gt; knuth
	 * Knuth, Donald         --&gt; d.knuth
	 * Knuth, Donald E.      --&gt; d.knuth
	 * Maarten de Rijke      --&gt; m.rijke
	 * Balby Marinho, Leandro--&gt; l.marinho
	 * </pre>
	 * 
	 * @param personName 
	 * @return The normalized person name as string. 
	 */
	public static String normalizePerson(final PersonName personName) {
		final String first = personName.getFirstName();
		final String last  = personName.getLastName();
		if (present(first) && !present(last)) {
			/*
			 * Only the first name is given. This should practically never happen,
			 * since we put such names into the last name field.
			 * 
			 */
			return StringUtils.removeNonNumbersOrLettersOrDotsOrCommaOrSpace(first).toLowerCase();
		}
		if (present(first) && present(last)) {
			/*
			 * First and last given - default.
			 * Take the first letter of the first name and append the last part
			 * of the last name.
			 */
			return getFirst(first) + "." + getLast(last);
		}
		if (present(last)) {
			/*
			 * Only last name available - could be a "regular" name enclosed
			 * in brackets.
			 */
			return getLast(last);
		}
		return "";
	}

	/**
	 * Returns the first letter of the first name, or an empty string, if no
	 * such letter exists.
	 * 
	 * @param first
	 * @return
	 */
	private static String getFirst(final String first) {
		final Matcher matcher = SINGLE_LETTER.matcher(first);
		if (matcher.find()) {
			return matcher.group(1).toLowerCase();
		}
		return "";
	}
	
	
	/**
	 * Extracts from the last name the last part and cleans it. I.e., from 
	 * "van de Gruyter" we get "gruyter"
	 * 
	 * @param last
	 * @return
	 */
	private static String getLast(final String last) {
		/*
		 * A name enclosed in brackets {Like this One} is detected as a single 
		 * last name. We here re-parse such names to extract the "real" name.
		 */
		final String trimmedLast = last.trim();
		if (trimmedLast.startsWith("{") && trimmedLast.endsWith("}")) {
			final List<PersonName> name = PersonNameUtils.discoverPersonNamesIgnoreExceptions(trimmedLast.substring(1, trimmedLast.length() - 1));
			if (present(name)) return normalizePerson(name.get(0));
		} 
		/*
		 * We remove all unusual characters.
		 */
		final String cleanedLast = StringUtils.removeNonNumbersOrLettersOrDotsOrCommaOrSpace(trimmedLast).toLowerCase().trim();
		/*
		 * If we find a space character, we take the last part of the name
		 */
		final int pos = cleanedLast.lastIndexOf(' ');
		return pos > 0 ? cleanedLast.substring(pos + 1) : cleanedLast;
	}
	
}