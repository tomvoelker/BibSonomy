/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model;

import lombok.Getter;
import lombok.Setter;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Serializable;
import java.util.Date;

/**
 * The name of a person.
 * 
 * @author Jens Illig
 */
@Getter
@Setter
public class PersonName implements Serializable {
	private static final long serialVersionUID = 4365762117878931642L;

	/** delimiter between the parts of a person's name in the "Last, First" format. */
	public static final String LAST_FIRST_DELIMITER = ",";

	private int personNameChangeId;
	/** firstname(s) of the person */
	private String firstName;
	/** lastname(s) of the person */
	private String lastName;
	private String personId;
	private boolean isMain;
	private Person person;
	private String changedBy;
	private Date changedAt;
	
	/**
	 * default bean constructor
	 */
	public PersonName() {
		
	}
	
	/**
	 * Sets name and extracts first and last name.
	 * @param lastName 
	 */
	public PersonName(final String lastName) {
		this.setLastName(lastName); 
	}
	
	/**
	 * @param firstName
	 * @param lastName
	 */
	public PersonName(final String firstName, final String lastName) {
		this.setFirstName(firstName);
		this.setLastName(lastName);  
	}
	

	@Override
	public String toString() {
		return this.lastName + LAST_FIRST_DELIMITER + (present(this.firstName)? " " + this.firstName : "");
	}
	
	/**
	 * @return serialized form
	 */
	public String serialize() {
		return this.lastName + LAST_FIRST_DELIMITER + (present(this.firstName)? this.firstName : "");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PersonName) {
			final PersonName other = (PersonName) obj;
			return equal(this.firstName, other.firstName) && equal(this.lastName, other.lastName);
		}
		return super.equals(obj);
	}
	
	public boolean equalsWithDetails(PersonName obj) {
		return equals(obj) && (this.isMain == obj.isMain);
	}
	
	private static boolean equal(final String a, final String b) {
		if (present(a)) return a.equals(b);
		if (present(b)) return b.equals(a);
		// both are either null or whitespace - we assume them to be equal
		return true;
	}
	
	@Override
	public int hashCode() {
		if (present(this.firstName)) {
			if (present(this.lastName)) {
				return this.firstName.hashCode() ^ this.lastName.hashCode();
			}
			return this.firstName.hashCode();
		}
		if (present(this.lastName)) {
			return this.lastName.hashCode();
		}
		return super.hashCode();
	}

}