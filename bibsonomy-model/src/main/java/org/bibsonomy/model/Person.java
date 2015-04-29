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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity class of a real person. Note that {@link User} and {@link Author} are
 * not {@link Person} subclasses since they are not modeled as real persons
 * instances.
 * 
 * @author jil
 */
public class Person implements Serializable {

	private static final long serialVersionUID = 4578956154246424767L;
	
	/** null means new non-persistent object */
	private String id;
	/** usually current real name */
	private PersonName mainName;
	/** other names like former names or pseudonyms */
	private List<PersonName> names;
	/** something like "Dr. rer. nat." */
	private String academicDegree;
	/** researcher id on http://orcid.org/ */
	private String orcid;
	/** sameAs relation to a user */
	private String user;
	/** {@link User} who last modified this {@link Person} */
	private String changedBy;
	/** point in time when the last change was made */
	private Date changeDate;
	/** a publication which disambiguates the person. Usually the person's thesis of highest degree such as a phd thesis. */
	private ResourcePersonRelation disambiguatingPublication;
	/** the number of posts in the system, which this {@link Person} as an author */
	private int postCounter;
	
	private List<ResourcePersonRelation> resourcePersonRelations;
	
	/**
	 * 
	 */
	public Person() {
		this.names = new ArrayList<PersonName>();
		this.resourcePersonRelations = new ArrayList<ResourcePersonRelation>();
	}
	
	/**
	 * @return synthetic id. null means new non-persistent object
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param string synthetic id. null means new non-persistent object
	 */
	public void setId(String string) {
		this.id = string;
		for(PersonName name : this.names)
			name.setPersonId(this.id);
	}

	/**
	 * @return usually current real name
	 */
	public PersonName getMainName() {
		if(this.mainName == null) {
			for(PersonName name : this.names) {
				if(name.isMain())
					this.mainName = name;
			}
		}
		return this.mainName;
	}

	/**
	 * @param int usually current real name
	 */
	public void setMainName(int id) {
		for(PersonName name : this.names) {
			if(name.getId() == id) {
				name.setMain(true);
				this.mainName = name;
			} else {
				name.setMain(false);
			}
		}
	}
	
	/**
	 * 
	 * @param PersonName name
	 */
	public void setMainName(PersonName name) {
		if(!this.names.contains(name)) {
			name.setPersonId(this.getId());
			this.names.add(name);
		}
		this.mainName = name;
	}
	/**
	 * 
	 * @param PersonName name
	 */
	public void addName(PersonName name) {
		if(this.getNames().contains(name))
			return;
		
		if(name != null)  {
			name.setPersonId(this.getId());
			name.setMain(false);
			this.getNames().add(name);
		}
	}
	
	/**
	 * 
	 * @param PersonName name
	 */
	public void removeName(PersonName name) {
		if(!this.getNames().contains(name))
			return;
		if(name != null) {
			this.names.remove(name);
		}
	}

	/**
	 * @return something like "Dr. rer. nat."
	 */
	public String getAcademicDegree() {
		return this.academicDegree;
	}

	/**
	 * @param scientificDegree something like "Dr. rer. nat."
	 */
	public void setAcademicDegree(String scientificDegree) {
		this.academicDegree = scientificDegree;
	}

	/**
	 * @return user who last modified this {@link Person}
	 */
	public String getChangedBy() {
		return this.changedBy;
	}

	/**
	 * @param modifiedBy user who last modified this {@link Person}
	 */
	public void setChangedBy(String modifiedBy) {
		this.changedBy = modifiedBy;
	}

	/**
	 * @return date of last modification
	 */
	public Date getChangeDate() {
		return this.changeDate;
	}

	/**
	 * @param modifiedAt date of last modification
	 */
	public void setChangeDate(Date modifiedAt) {
		this.changeDate = modifiedAt;
	}
	/**
	 * @return the names
	 */
	public List<PersonName> getNames() {
		return this.names;
	}

	/**
	 * @param names the names to set
	 */
	public void setNames(List<PersonName> names) {
		this.names = names;
	}

	@Override
	public boolean equals(Object obj) {
		if (id == null) {
			return obj == this;
		}
		return ((obj instanceof Person) && (this.getId().equals(((Person)obj).getId())));
	}
	
	@Override
	public int hashCode() {
		if (id == null) {
			return System.identityHashCode(this);
		}
		return id.hashCode();
	}

	/** 
	 * @return {@link User} in sameAs relation to this {@link Person} 
	 */
	public String getUser() {
		return this.user;
	}

	/**
	 * @param user {@link User} in sameAs relation to this {@link Person} 
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return researcher id on http://orcid.org/
	 */
	public String getOrcid() {
		return this.orcid;
	}

	/**
	 * @param orcid researcher id on http://orcid.org/
	 */
	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}

	/**
	 * @return a publication which disambiguates the person. Usually the person's thesis of highest degree such as a phd thesis.
	 */
	public ResourcePersonRelation getDisambiguatingPublication() {
		return this.disambiguatingPublication;
	}

	/**
	 * @param disambiguatingPublication a publication which disambiguates the person. Usually the person's thesis of highest degree such as a phd thesis.
	 */
	public void setDisambiguatingPublication(ResourcePersonRelation disambiguatingPublication) {
		this.disambiguatingPublication = disambiguatingPublication;
	}

	/**
	 * @return the number of posts in the system, which this {@link Person} as an author
	 */
	public int getPostCounter() {
		return this.postCounter;
	}

	/**
	 * @param postCounter the number of posts in the system, which this {@link Person} as an author
	 */
	public void setPostCounter(int postCounter) {
		this.postCounter = postCounter;
	}
	
	/**
	 * 
	 * @param academicDegree
	 * @return Person
	 */
	public Person withAcademicDegree(String academicDegree) {
		this.setAcademicDegree(academicDegree);
		return this;	
	}

	/**
	 * @param formUser
	 * @return Person
	 */
	public Person withUser(String user) {
		this.setUser(user);
		return this;
	}

	/**
	 * @param withMain
	 * @return Person
	 */
	public Person withMainName(PersonName withMain) {
		this.setMainName(withMain);
		return this;
	}
	

	/**
	 * @return the resourcePersonRelations
	 */
	public List<ResourcePersonRelation> getResourcePersonRelations() {
		return this.resourcePersonRelations;
	}

	/**
	 * @param resourcePersonRelations the resourcePersonRelations to set
	 */
	public void setResourcePersonRelations(List<ResourcePersonRelation> resourcePersonRelations) {
		this.resourcePersonRelations = resourcePersonRelations;
	}

}
