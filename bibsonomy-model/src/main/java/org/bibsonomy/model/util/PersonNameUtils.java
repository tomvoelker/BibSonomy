package org.bibsonomy.model.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.bibsonomy.model.PersonName;


/**
 * Nice place be for static util methods regarding names of persons
 *
 * @version $Id$
 * @author  Jens Illig
 * $Author$
 */
public class PersonNameUtils {
	/**
	 * analyses a string of name of the form J. T. Kirk and M. Scott and ...
	 * @param authorField the source string 
	 * @return the result
	 */
	public static List<PersonName> extractList(final String authorField) {
		final List<PersonName> authors = new LinkedList<PersonName>();
		if (authorField != null) {
			final Scanner t = new Scanner(authorField);
			t.useDelimiter(" and ");
			while (t.hasNext()) {
				final PersonName a = new PersonName();
				a.setName(t.next());
				authors.add(a);
			}
		}
		return authors;
	}
}
