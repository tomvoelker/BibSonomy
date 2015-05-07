package org.bibsonomy.model;

import org.bibsonomy.model.enums.PersonResourceRelationType;

/**
 * TODO: add documentation to this class
 *
 * @author Chris
 */
public class ResourcePersonRelation {
	private int id;
	private PersonResourceRelationType relationType;
	private int qualifying;
	private Person person;
	/** name of the person who created this link */
	private String createdByUserName;
	private Post<BibTex> post;
	/** the position in the resource's list of authors / editors / ... */
	private int personIndex;
	
	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
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
	public Post<BibTex> getPost() {
		return this.post;
	}
	/**
	 * @param post the post to set
	 */
	public void setPost(Post<BibTex> post) {
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
	public String getCreatedByUserName() {
		return this.createdByUserName;
	}
	public void setCreatedByUserName(String createdByUserName) {
		this.createdByUserName = createdByUserName;
	}
	
}
