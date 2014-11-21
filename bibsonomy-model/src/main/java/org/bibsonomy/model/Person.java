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
import java.util.Set;

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
	private Integer id;
	/** usually current real name */
	private PersonName mainName;
	/** other names like former names or pseudonyms */
	private Set<PersonName> alternateNames = new HashSet<PersonName>();
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
	private BibTex disambiguatingPublication;
	/** the number of posts in the system, which this {@link Person} as an author */
	private int postCounter;
	
	/**
	 * @return synthetic id. null means new non-persistent object
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * @param id synthetic id. null means new non-persistent object
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return usually current real name
	 */
	public PersonName getMainName() {
		return this.mainName;
	}

	/**
	 * @param name usually current real name
	 */
	public void setMainName(PersonName name) {
		this.mainName = name;
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
		if (id == null) {
			return obj == this;
		}
		return ((obj instanceof Person) && (this.getId() == ((Person)obj).getId()));
	}
	
	@Override
	public int hashCode() {
		if (id == null) {
			return System.identityHashCode(this);
		}
		return id;
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
	public BibTex getDisambiguatingPublication() {
		return this.disambiguatingPublication;
	}

	/**
	 * @param disambiguatingPublication a publication which disambiguates the person. Usually the person's thesis of highest degree such as a phd thesis.
	 */
	public void setDisambiguatingPublication(BibTex disambiguatingPublication) {
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

}
