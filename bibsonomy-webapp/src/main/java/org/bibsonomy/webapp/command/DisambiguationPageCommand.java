package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;

/**
 * @author Christian Pfeiffer
 */
public class DisambiguationPageCommand extends UserResourceViewCommand {

	private String requestedHash;
	private String requestedRole;
	private int requestedIndex;
	
	private String formPersonId;
	private String formPersonNameId;
	
	private List<PersonName> suggestedPersonNames;
	private Person person;
	private PersonName personName;
	private Post<BibTex> post;

	/**
	 * @return the suggestedPersons
	 */
	public List<PersonName> getSuggestedPersonNames() {
		return this.suggestedPersonNames;
	}

	/**
	 * @param suggestedPersons the suggestedPersons to set
	 */
	public void setSuggestedPersonNames(List<PersonName> suggestedPersonNames) {
		this.suggestedPersonNames = suggestedPersonNames;
	}

	/**
	 * @return the requestedHash
	 */
	public String getRequestedHash() {
		return this.requestedHash;
	}

	/**
	 * @param requestedHash the requestedHash to set
	 */
	public void setRequestedHash(String requestedHash) {
		this.requestedHash = requestedHash;
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
	 * @return the person
	 */
	public Person getPerson() {
		return this.person;
	}

	/**
	 * @param person the person to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * @return
	 */
	public String getRequestedRole() {
		return this.requestedRole;
	}

	/**
	 * @param requestedRole the requestedRole to set
	 */
	public void setRequestedRole(String requestedRole) {
		this.requestedRole = requestedRole;
	}

	/**
	 * @return the formAddPersonId
	 */
	public String getFormPersonId() {
		return this.formPersonId;
	}

	/**
	 * @param formAddPersonId the formAddPersonId to set
	 */
	public void setFormPersonId(String formPersonId) {
		this.formPersonId = formPersonId;
	}

	/**
	 * @return the formPersonNameId
	 */
	public String getFormPersonNameId() {
		return this.formPersonNameId;
	}

	/**
	 * @param formPersonNameId the formPersonNameId to set
	 */
	public void setFormPersonNameId(String formPersonNameId) {
		this.formPersonNameId = formPersonNameId;
	}

	/**
	 * @return the personName
	 */
	public PersonName getPersonName() {
		return this.personName;
	}

	/**
	 * @param personName the personName to set
	 */
	public void setPersonName(PersonName personName) {
		this.personName = personName;
	}

	/**
	 * @return the requestedIndex
	 */
	public int getRequestedIndex() {
		return this.requestedIndex;
	}

	/**
	 * @param requestedIndex the requestedIndex to set
	 */
	public void setRequestedIndex(int requestedIndex) {
		this.requestedIndex = requestedIndex;
	}
}
