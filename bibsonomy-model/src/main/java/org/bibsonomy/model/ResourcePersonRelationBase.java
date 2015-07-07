package org.bibsonomy.model;

import java.util.Date;

import org.bibsonomy.model.enums.PersonResourceRelationType;

/**
 * TODO: add documentation to this class
 *
 * @author Chris
 */
// TODO rename to PersonResourceRelation
public abstract class ResourcePersonRelationBase {
	private int personRelChangeId;
	private PersonResourceRelationType relationType;
	private int qualifying;
	/** name of the person who created this link */
	private String changedBy;
	private Date changedAt;

	/** the position in the resource's list of authors / editors / ... */
	private int personIndex;
	
	/**
	 * @return the id
	 */
	public int getPersonRelChangeId() {
		return this.personRelChangeId;
	}
	/**
	 * @param id the id to set
	 */
	public void setPersonRelChangeId(int id) {
		this.personRelChangeId = id;
	}

	/**
	 * @return the qualifying
	 */
	public int getQualifying() {
		return this.qualifying;
	}
	/**
	 * @param qualifying the qualifying to set
	 */
	public void setQualifying(int qualifying) {
		this.qualifying = qualifying;
	}

	/**
	 * @return the authorIndex
	 */
	public int getPersonIndex() {
		return this.personIndex;
	}
	/**
	 * @param authorIndex the authorIndex to set
	 */
	public void setPersonIndex(int authorIndex) {
		this.personIndex = authorIndex;
	}
	public PersonResourceRelationType getRelationType() {
		return this.relationType;
	}
	public void setRelationType(PersonResourceRelationType relationType) {
		this.relationType = relationType;
	}
	public String getChangedBy() {
		return this.changedBy;
	}
	public void setChangedBy(String createdByUserName) {
		this.changedBy = createdByUserName;
	}
	public Date getChangedAt() {
		return this.changedAt;
	}
	public void setChangedAt(Date changedAt) {
		this.changedAt = changedAt;
	}
	
}
