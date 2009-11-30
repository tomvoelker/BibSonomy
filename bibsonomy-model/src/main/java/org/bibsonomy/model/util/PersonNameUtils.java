/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.bibsonomy.model.PersonName;

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
	 * Analyses a string of name of the form "J. T. Kirk" and "M. Scott"
	 * 
	 * @param authorField the source string 
	 * @return the result
	 */
	public static List<PersonName> extractList(final String authorField) {
		final List<PersonName> authors = new LinkedList<PersonName>();
		if (authorField != null) {
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
}