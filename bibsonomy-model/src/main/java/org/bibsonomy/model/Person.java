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

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.model.enums.PersonResourceRelation;

/**
 * Entity class of a real person. Note that {@link User} and {@link Author} are
 * not {@link Person} subclasses since they are not modeled as real persons
 * instances.
 * 
 * @author jil
 */
public class Person implements Serializable {

	private static final long serialVersionUID = 4578956154246424767L;
	
	private int id;
	/** usually current real name */
	private PersonName mainName;
	/** other names like former names or pseudonyms */
	private Set<PersonName> alternateNames;
	private String academicDegree;
	private User modifiedBy;
	private Date modifiedAt;
	private Map<PersonResourceRelation, List<BibTex>> relatedPublications;
	
	public Person() {
		this.alternateNames = new HashSet<PersonName>();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PersonName getMainName() {
		return this.mainName;
	}

	public void setMainName(PersonName name) {
		this.mainName = name;
	}

	public String getAcademicDegree() {
		return this.academicDegree;
	}

	public void setAcademicDegree(String scientificDegree) {
		this.academicDegree = scientificDegree;
	}

	public User getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedAt() {
		return this.modifiedAt;
	}

	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
	/**
	 * @return the names
	 */
	public Set<PersonName> getAlternateNames() {
		return this.alternateNames;
	}

	/**
	 * @param names the names to set
	 */
	public void setAlternateNames(Set<PersonName> names) {
		this.alternateNames = names;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Person) {
			final Person other = (Person) obj;
			for(PersonName pn : other.getAlternateNames()) {
				if(!this.getAlternateNames().contains(pn))
					return false;
			}
			for(PersonName pn: this.getAlternateNames()) {
				if(!other.getAlternateNames().contains(pn))
					return false;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		for(PersonName pn : this.getAlternateNames()){
			hash = hash ^ pn.getFirstName().hashCode() ^ pn.getLastName().hashCode();
		}
		return hash;
	}
}
