package org.bibsonomy.model;

import java.util.Date;

import org.bibsonomy.model.enums.PersonResourceRelationType;

/**
 * TODO: add documentation to this class
 *
 * @author Chris
 */
public class ResourcePersonRelation {
	private int personChangeId;
	private PersonResourceRelationType relationType;
	private int qualifying;
	private Person person;
	/** name of the person who created this link */
	private String changedBy;
	private Date changedAt;
	private Post<? extends BibTex> post;
	/** the position in the resource's list of authors / editors / ... */
	private int personIndex;
	
	/**
	 * @return the id
	 */
	public int getPersonChangeId() {
		return this.personChangeId;
	}
	/**
	 * @param id the id to set
	 */
	public void setPersonChangeId(int id) {
		this.personChangeId = id;
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
	 * @return the personName
	 */
	public Person getPerson() {
		return this.person;
	}
	/**
	 * @param person the {@link Person} to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * @return the post
	 */
	public Post<? extends BibTex> getPost() {
		return this.post;
	}
	/**
	 * @param post the post to set
	 */
	public void setPost(Post<? extends BibTex> post) {
		this.post = post;
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
