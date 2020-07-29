/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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

import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.enums.Gender;
import org.bibsonomy.model.extra.AdditionalKey;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Entity class of a real person. Note that {@link User} and {@link Author} are
 * not {@link Person} subclasses since they are not modeled as real persons
 * instances.
 *
 * @author jil
 */
public class Person implements Linkable, Serializable {

	private static final long serialVersionUID = 4578956154246424767L;
	public static final String[] fieldsWithResolvableMergeConflicts = {"mainName", "academicDegree", "orcid", "researcherid", "gender", "college", "email", "homepage"};

	private Integer personChangeId;
	/** null means new non-persistent object */
	private String personId;
	/** usually current real name */
	private PersonName mainName;
	/** other names like former names or pseudonyms */
	private List<PersonName> names;
	/** something like "Dr. rer. nat." */
	private String academicDegree;
	/** researcher id on http://orcid.org/ */
	private String orcid;
	/** researcher id on http://researcherID.com/ */
	private String researcherid;
	/** sameAs relation to a user */
	private String user;
	/** {@link User} who last modified this {@link Person} */
	private String changedBy;
	/** point in time when the last change was made */
	private Date changeDate;
	/** the number of posts in the system, which this {@link Person} as an author */
	private int postCounter;
	/** place to link to the original entries when imported from Deutsche Nationalbibliothek */
	private String dnbPersonId;
	/** the gender */
	private Gender gender;
	/** the college of the person */
	private String college;
	/** the email of the person */
	private String email;
	/** the homepage of the person */
	private URL homepage;

	/** cris links that are connected to this project */
	private List<CRISLink> crisLinks = new LinkedList<>();

	private List<ResourcePersonRelation> resourceRelations = new LinkedList<>();

	/** additional keys for person */
	private List<AdditionalKey> additionalKeys = new LinkedList<>();
	/**
	 * default constructor
	 */
	public Person() {
		this.names = new ArrayList<>();
	}

	/**
	 * @return synthetic id. null means new non-persistent object
	 */
	public String getPersonId() {
		return this.personId;
	}

	/**
	 * @param string synthetic id. null means new non-persistent object
	 */
	public void setPersonId(String string) {
		this.personId = string;
		for (PersonName name : this.names)
			name.setPersonId(this.personId);
	}

	/**
	 * @return usually current real name
	 */
	public PersonName getMainName() {
		if (this.mainName == null) {
			for (PersonName name : this.names) {
				if (name.isMain()) {
					this.mainName = name;
				}
			}
		}
		return this.mainName;
	}

	/**
	 * @param id usually current real name
	 */
	public void setMainName(int id) {
		for (PersonName name : this.names) {
			if (name.getPersonNameChangeId() == id) {
				name.setMain(true);
				this.mainName = name;
			} else {
				name.setMain(false);
			}
		}
	}

	/**
	 *
	  @param name
	 */
	public void setMainName(PersonName name) {
		if (!this.names.contains(name)) {
			name.setPersonId(this.getPersonId());
			name.setMain(true);
			this.names.add(name);
		}
		this.mainName = name;
	}

	/**
	 *
	 * @param name name
	 */
	public void addName(PersonName name) {
		if(this.getNames().contains(name)) {
			return;
		}

		if (name != null) {
			name.setPersonId(this.getPersonId());
			name.setMain(false);
			this.getNames().add(name);
		}
	}

	/**
	 *
	 * @param name the person name to remove
	 */
	public void removeName(PersonName name) {
		if (!this.getNames().contains(name)) {
			return;
		}
		if (name != null) {
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
		if (personId == null) {
			return obj == this;
		}

		return ((obj instanceof Person) && (this.getPersonId().equals(((Person) obj).getPersonId())));
	}

	@Override
	public int hashCode() {
		if (personId == null) {
			return System.identityHashCode(this);
		}
		return personId.hashCode();
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
	 * @return researcher id on http://researcherID.com/
	 */
	public String getResearcherid() {
		return this.researcherid;
	}

	/**
	 * @param researcherid researcher id on http://researcherID.com/
	 */
	public void setResearcherid(String researcherid) {
		this.researcherid = researcherid;
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


	public Integer getPersonChangeId() {
		return this.personChangeId;
	}

	public void setPersonChangeId(Integer personChangeId) {
		this.personChangeId = personChangeId;
	}

	public String getDnbPersonId() {
		return this.dnbPersonId;
	}

	public void setDnbPersonId(String dnbPersonId) {
		this.dnbPersonId = dnbPersonId;
	}

	public Gender getGender() {
		return this.gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	/**
	 * @return the college
	 */
	public String getCollege() {
		return this.college;
	}

	/**
	 * @param college the college to set
	 */
	public void setCollege(String college) {
		this.college = college;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the homepage
	 */
	public URL getHomepage() {
		return this.homepage;
	}

	/**
	 * @param homepage the homepage to set
	 */
	public void setHomepage(URL homepage) {
		this.homepage = homepage;
	}

	/**
	 * @return the crisLinks
	 */
	public List<CRISLink> getCrisLinks() {
		return crisLinks;
	}

	/**
	 * @param crisLinks the crisLinks to set
	 */
	public void setCrisLinks(List<CRISLink> crisLinks) {
		this.crisLinks = crisLinks;
	}

	/**
	 * @return the resourceRelations
	 */
	public List<ResourcePersonRelation> getResourceRelations() {
		return resourceRelations;
	}

	/**
	 * @param resourceRelations the resourceRelations to set
	 */
	public void setResourceRelations(List<ResourcePersonRelation> resourceRelations) {
		this.resourceRelations = resourceRelations;
	}

	/**
	 * @return	list of additional keys
	 */
	public List<AdditionalKey> getAdditionalKeys() {
		return additionalKeys;
	}

	/**
	 * @param additionalKeys to set
	 */
	public void setAdditionalKeys(List<AdditionalKey> additionalKeys) {
		this.additionalKeys = additionalKeys;
	}

	/**
	 * Get the person's additional key specified by the key name
	 *
	 * @param keyName	the key name
	 * @return			the additional key as an object, null if not found
	 */
	public AdditionalKey getAdditionalKey(String keyName) {
		for (AdditionalKey additionalKey : this.additionalKeys) {
			if (additionalKey.getKeyName().equalsIgnoreCase(keyName)) return additionalKey;
		}
		return null;
	}

	/**
	 * Add a new additional key to the person with key name and value
	 *
	 * @param keyName	the key name
	 * @param keyValue	the key value
	 * @return 			true, if added or exact same key was already present, false if key couldn't be added
	 */
	public boolean addAdditionalKey(String keyName, String keyValue) {
		AdditionalKey additionalKey = new AdditionalKey(keyName, keyValue);
		return addAdditionalKey(additionalKey);
	}

	/**
	 * Add a new additional key to the person with an additional key object
	 *
	 * @param additionalKey		the additional key object
	 * @return 					true, if added or exact same key was already present, false if key couldn't be added
	 */
	public boolean addAdditionalKey(AdditionalKey additionalKey) {
		AdditionalKey foundKey = getAdditionalKey(additionalKey.getKeyName());

		if (foundKey != null) {
			// Return true, if exact same key was already present
			// Return false, if key name with different value was found
			return foundKey.getKeyValue().equals(additionalKey.getKeyName());
		} else {
			// Adding key
			this.additionalKeys.add(additionalKey);
			return true;
		}
	}

	/**
	 * Remove an additional key from the person specified by the key name
	 *
	 * @param keyName	the key name
	 * @return true, if removing it was successful or key wasn't present. false otherwise
	 */
	public boolean removeAdditionalKey(String keyName) {
		AdditionalKey foundKey = getAdditionalKey(keyName);
		if (foundKey != null) {
			this.additionalKeys.remove(foundKey);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Update an additional key of the person specified by the key name
	 *
	 * @param keyName	the key name
	 * @param keyValue	the new key value
	 * @return			true, if additional key was updated. False, if key wasn't found to update
	 */
	public boolean updateAdditionalKey(String keyName, String keyValue) {
		AdditionalKey foundKey = getAdditionalKey(keyName);
		if (foundKey != null) {
			foundKey.setKeyValue(keyValue);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getLinkableId() {
		return this.getPersonId();
	}

	@Override
	public Integer getId() {
		return this.personChangeId;
	}

	/**
	 * returns true if specific attributes are equal or at least null for one person
	 * @param person
	 * @return
	 */
	public boolean equalsTo(Person person) {
		return (this.academicDegree == null || person.getAcademicDegree() == null || this.academicDegree.equals(person.getAcademicDegree())) &&
						(this.college == null || person.getCollege() == null || this.college.equals(person.getCollege())) &&
						(this.gender == null || person.getGender() == null || this.gender.equals(person.getGender())) &&
						(this.email == null || person.getEmail() == null || this.email.equals(person.getEmail())) &&
						(this.homepage == null || person.getHomepage() == null || this.homepage.equals(person.getHomepage())) &&
						(this.orcid == null || person.orcid == null || this.orcid.equals(person.orcid)) &&
						(this.researcherid == null || person.researcherid == null || this.researcherid.equals(person.researcherid)) &&
						(this.user == null || person.user == null);
	}
}
