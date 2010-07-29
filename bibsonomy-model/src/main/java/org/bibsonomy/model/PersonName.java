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

package org.bibsonomy.model;

import org.bibsonomy.model.util.PersonNameUtils;


/**
 * The name of a person.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class PersonName {

	private String name;
	private String firstName;
	private String lastName;

	/**
	 * Default constructor
	 */
	public PersonName() {
		// nothing to do
	}
	
	/**
	 * Sets name and extracts first and last name.
	 * 
	 * @param name
	 */
	public PersonName(final String name) {
		this.setName(name);
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
	 * @return the full name of the person
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * sets the full name and tries to set first- and lastname from extracted values also. 
	 * @param name the full name of the person
	 */
	public void setName(String name) {
		this.name = name;
		PersonNameUtils.discoverFirstAndLastName(name, this);
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


}