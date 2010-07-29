/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
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

package org.bibsonomy.model.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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
	 * delimiter between the parts of a person's name in the "Last, First" format.
	 * 
	 */
	public static final String LAST_FIRST_DELIMITER = ",";
	/**
	 * the delimiter used for separating person names
	 */
	public static final String PERSON_NAME_DELIMITER = " and ";


	/**
	 * Analyses a string of names of the form "J. T. Kirk and M. Scott"
	 * 
	 * @param authorField the source string 
	 * @return the result
	 */
	public static List<PersonName> extractList(final String authorField) {
		final List<PersonName> authors = new LinkedList<PersonName>();
		if (present(authorField)) {
			final Scanner t = new Scanner(authorField);
			t.useDelimiter(PERSON_NAME_DELIMITER);
			while (t.hasNext()) {
				final PersonName a = new PersonName();
				a.setName(t.next());
				authors.add(a);
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
			final int indexOf = name.indexOf(LAST_FIRST_DELIMITER);
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
	 * The string is only changed, if it contains at least one comma.
	 * 
	 * @param names
	 * @return a list of person names, where each name is in the "First Last" format. 
	 */
	public static String lastFirstToFirstLastMany(final String names) {
		if (present(names)) {
			if (names.contains(LAST_FIRST_DELIMITER)) {
				final StringBuilder namesNew = new StringBuilder();
				
				final String[] split = names.split(PERSON_NAME_DELIMITER);
				for (int i = 0; i < split.length; i++) {
					namesNew.append(lastFirstToFirstLast(split[i]));
					if (i < split.length - 1) namesNew.append(PERSON_NAME_DELIMITER);
				}
				return namesNew.toString();
			}
		}
		return names;
	}

	/**
	 * Tries to detect the firstname and lastname of each author or editor.
	 * 
	 * @param name 
	 * @param personName 
	 */
	public static void discoverFirstAndLastName(final String name, final PersonName personName) {
		if (present(name)) {
			/*
			 * DBLP author names sometimes contain numbers (when there are
			 * several authors with the same name. Here we remove those numbers
			 */
			final String cleanedName = StringUtils.removeSingleNumbers(name);
			/*
			 * Names can be in to formats:
			 * 
			 * First Last
			 * 
			 * or 
			 * 
			 * Last, First
			 * 
			 * If the name contains a comma ",", we assume the latter
			 */
			final int indexOfComma = cleanedName.indexOf(LAST_FIRST_DELIMITER);
			if (indexOfComma >= 0) {
				/*
				 * We assume Last, First - it's clear, which part is what
				 */
				personName.setFirstName(cleanedName.substring(indexOfComma + 1).trim());
				personName.setLastName(cleanedName.substring(0, indexOfComma).trim());
				return;
			}
			/*
			 * First Last ... its not so obvious, which part is what. 
			 * 
			 * We assume that a name has either only one (abbreviated or not) Firstname,
			 * or several - while all except the first must be abbreviated. The last
			 * name then begins at the first word that does not contain a ".".  
			 * 
			 * Firstnames can be abbreviated with a '.' to be identified as firstnames.
			 */

			/*
			 * first: split at whitespace
			 */
			final String[] nameList = cleanedName.split("\\s+");
			/*
			 * detect firstname and lastname
			 */
			final StringBuilder firstNameBuilder = new StringBuilder();
			int i = 0;
			while (i < nameList.length - 1) { // iterate up to the last but one part
				final String part = nameList[i++];
				firstNameBuilder.append(part + " ");
				/*
				 * stop, if this is the last abbreviated forename 
				 */
				if (part.contains(".") && !nameList[i].contains(".")) {
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
	}

	/**
	 * Tries to extract the last name of the first person. 
	 * 
	 * @param person some string representation of a list of persons with their first- and lastnames  
	 * @return the last name of the first person
	 */
	public static String getFirstPersonsLastName(final String person) {
		if (person != null) {
			final String firstauthor;
			/*
			 * check, if there is more than one author
			 */
			final int firstand = person.indexOf(PERSON_NAME_DELIMITER);
			if (firstand < 0) {
				firstauthor = person;
			} else {
				firstauthor = person.substring(0, firstand);				
			}
			/*
			 * first author extracted, get its last name
			 */
			final int lastspace = firstauthor.lastIndexOf(' ');
			final String lastname;
			if (lastspace < 0) {
				lastname = firstauthor;
			} else {
				lastname = firstauthor.substring(lastspace + 1, firstauthor.length());
			}
			return lastname;
		}
		return null;
	}	

}