/**
 * BibSonomy-gImporter - exports from dnb2, imports to bibsonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.dnbimport.model;

/**
 * TODO: add documentation to this class
 * 
 * @author jensi
 */
public class DnbPerson {
	private String personId;
	
	private String uniquePersonId;
	
	private String firstName;

	private String lastName;

	private String personFunction;
	
	private boolean diffPerson;
	
	private String gender;

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPersonFunction() {
		return this.personFunction;
	}

	public void setPersonFunction(String personFunction) {
		this.personFunction = personFunction;
	}

	public boolean isDiffPerson() {
		return this.diffPerson;
	}

	public void setDiffPerson(boolean diffPerson) {
		this.diffPerson = diffPerson;
	}

	public String getPersonId() {
		return this.personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getUniquePersonId() {
		return this.uniquePersonId;
	}

	public void setUniquePersonId(String uniquePersonId) {
		this.uniquePersonId = uniquePersonId;
	}

}
