/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Serializable;
import java.util.Date;


/**
 * The name of a person.
 * 
 * @author Jens Illig
 */
public class PersonName implements Serializable {
	private static final long serialVersionUID = 4365762117878931642L;

	/** delimiter between the parts of a person's name in the "Last, First" format. */
	public static final String LAST_FIRST_DELIMITER = ",";

	private int personNameChangeId;
	private String firstName;
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
	
	/**
	 * @return the firstname(s) of the person
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * @return the lastname(s) of the person
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	/**
	 * @return the id
	 */
	public int getPersonNameChangeId() {
		return this.personNameChangeId;
	}

	/**
	 * @param id the id to set
	 */
	public void setPersonNameChangeId(int id) {
		this.personNameChangeId = id;
	}

	/**
	 * @return the personId
	 */
	public String getPersonId() {
		return this.personId;
	}

	/**
	 * @param personId the personId to set
	 */
	public void setPersonId(String personId) {
		this.personId = personId;
	}

	/**
	 * @return the isMain
	 */
	public boolean isMain() {
		return this.isMain;
	}

	/**
	 * @param isMain the isMain to set
	 */
	public void setMain(boolean isMain) {
		this.isMain = isMain;
	}
	
	/**
	 * @return the person
	 */
	public Person getPerson() {
		return this.person;
	}

	/**
	 * @param person the person to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	public String getChangedBy() {
		return this.changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

	public Date getChangedAt() {
		return this.changedAt;
	}

	public void setChangedAt(Date changedAt) {
		this.changedAt = changedAt;
	}

}