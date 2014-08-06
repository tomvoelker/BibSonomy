package org.bibsonomy.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.enums.PersonResourceRelation;

/**
 * Entity class of a real person. Note that {@link User} and {@link Author} are
 * not {@link Person} subclasses since they are not modeled as real persons
 * instances.
 * 
 * @author jil
 */
public class Person {
	private int id;
	private PersonName name;
	private String scientificDegree;
	private User modifiedBy;
	private Date modifiedAt;
	private Map<PersonResourceRelation, List<BibTex>> relatedPublications;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PersonName getName() {
		return this.name;
	}

	public void setName(PersonName name) {
		this.name = name;
	}

	public String getScientificDegree() {
		return this.scientificDegree;
	}

	public void setScientificDegree(String scientificDegree) {
		this.scientificDegree = scientificDegree;
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

}
