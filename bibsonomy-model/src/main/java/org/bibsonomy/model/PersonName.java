/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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

package org.bibsonomy.model;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * The name of a person.
 * 
 * @author Jens Illig
 */
public class PersonName implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4365762117878931642L;

	/**
	 * delimiter between the parts of a person's name in the "Last, First" format.
	 * 
	 */
	public static final String LAST_FIRST_DELIMITER = ",";

	private int id;
	private String firstName;
	private String lastName;
	private int personId;
	private boolean isMain;
	private Person person;
	private List<ResourcePersonRelation> rprs;

	/**
	 * Default constructor
	 */
	public PersonName() {
		rprs = new ArrayList<ResourcePersonRelation>();
	}
	
	/**
	 * Sets name and extracts first and last name.
	 * @param firstName 
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
	 * @return
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
	 * @param firstName
	 * @return
	 */
	public PersonName withFirstName(String firstName) {
		this.setFirstName(firstName);
		return this;
	}
	
	/**
	 * @param lastName
	 * @return
	 */
	public PersonName withLastName(String lastName) {
		this.setLastName(lastName);
		return this;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the personId
	 */
	public int getPersonId() {
		return this.personId;
	}

	/**
	 * @param personId the personId to set
	 */
	public void setPersonId(int personId) {
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
	 * @param b
	 * @return PersonName
	 */
	public PersonName withMain(boolean b) {
		this.setMain(b);
		return this;
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

	/**
	 * @return PersonName
	 */
	public PersonName withPersonId(int personId) {
		this.setPersonId(personId);
		return this;
	}

	/**
	 * @return the rprs
	 */
	public List<ResourcePersonRelation> getRprs() {
		return this.rprs;
	}

	/**
	 * @param rprs the rprs to set
	 */
	public void setRprs(List<ResourcePersonRelation> rprs) {
		this.rprs = rprs;
	}

}