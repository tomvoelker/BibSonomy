package org.bibsonomy.model.comparators;

import java.util.Comparator;

import org.bibsonomy.model.PersonName;

/**
 * @author dzo
 * @version $Id$
 */
public class PersonNameComparator implements Comparator<PersonName> {

	@Override
	public int compare(final PersonName personName, final PersonName otherPersonName) {
		/*
		 * first compare by last name
		 */
		final int lastNameCompare = personName.getLastName().compareTo(otherPersonName.getLastName());
		
		if (lastNameCompare != 0) {
			return lastNameCompare;
		}
		
		/*
		 * if both last name are identical sort by first name
		 */
		return personName.getFirstName().compareTo(otherPersonName.getFirstName());
	}
}
