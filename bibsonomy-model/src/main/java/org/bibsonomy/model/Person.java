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

import java.awt.List;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * A Person
 * 
 * @author Christian Pfeiffer
 */
public class Person implements Serializable {

	private static final long serialVersionUID = 4578956154246424767L;
	
	private String id;

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	private String graduation;
	/**
	 * @return the graduation
	 */
	public String getGraduation() {
		return this.graduation;
	}

	/**
	 * @param graduation the graduation to set
	 */
	public void setGraduation(String graduation) {
		this.graduation = graduation;
	}

	private Author author;
	/**
	 * @return the author
	 */
	public Author getAuthor() {
		return this.author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(Author author) {
		this.author = author;
	}

	/**
	 * @return the names
	 */
	public Set<PersonName> getNames() {
		return this.names;
	}

	/**
	 * @param names the names to set
	 */
	public void setNames(Set<PersonName> names) {
		this.names = names;
	}

	private Set<PersonName> names;

	/**
	 * Default constructor
	 */
	public Person() {}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Person) {
			final Person other = (Person) obj;
			for(PersonName pn : other.getNames()) {
				if(!this.getNames().contains(pn))
					return false;
			}
			for(PersonName pn: this.getNames()) {
				if(!other.getNames().contains(pn))
					return false;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		for(PersonName pn : this.getNames()){
			hash = hash ^ pn.getFirstName().hashCode() ^ pn.getLastName().hashCode();
		}
		return hash;
	}

}