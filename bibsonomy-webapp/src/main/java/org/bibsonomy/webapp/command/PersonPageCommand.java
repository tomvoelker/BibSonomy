package org.bibsonomy.webapp.command;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author Christian Pfeiffer
 */
public class PersonPageCommand extends UserResourceViewCommand {

	private String requestedPersonId;
	private String requestedHash;
	private String requestedAction;
	
	private PersonName formSelectedName;
	private String formGraduation;
	private String formGivenName;
	private String formSurName;
	private String formRole;
	
	private Person person;
	private Post<? extends Resource> post;

	/**
	 * @return the formSelectedName
	 */
	public PersonName getFormSelectedName() {
		return this.formSelectedName;
	}

	/**
	 * @param formSelectedName the formSelectedName to set
	 */
	public void setFormSelectedName(PersonName formSelectedName) {
		this.formSelectedName = formSelectedName;
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
	 * @return the formGraduation
	 */
	public String getFormGraduation() {
		return this.formGraduation;
	}

	/**
	 * @param formGraduation the formGraduation to set
	 */
	public void setFormGraduation(String formGraduation) {
		this.formGraduation = formGraduation;
	}
	
	/**
	 * @return the formGivenName
	 */
	public String getFormGivenName() {
		return this.formGivenName;
	}

	/**
	 * @param formGivenName the formGivenName to set
	 */
	public void setFormGivenName(String formGivenName) {
		this.formGivenName = formGivenName;
	}

	/**
	 * @return the formSurName
	 */
	public String getFormSurName() {
		return this.formSurName;
	}

	/**
	 * @param formSurName the formSurName to set
	 */
	public void setFormSurName(String formSurName) {
		this.formSurName = formSurName;
	}

	/**
	 * @return
	 */
	public String getRequestedPersonId() {
		return this.requestedPersonId;
	}

	/**
	 * @return the requestedAction
	 */
	public String getRequestedAction() {
		return this.requestedAction;
	}

	/**
	 * @param requestedAction the requestedAction to set
	 */
	public void setRequestedAction(String requestedAction) {
		this.requestedAction = requestedAction;
	}
	
	/**
	 * @param person
	 */
	public void setRequestedPersonId(String personId) {
		this.requestedPersonId = personId;
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
	 * @return the formRole
	 */
	public String getFormRole() {
		return this.formRole;
	}

	/**
	 * @param formRole the formRole to set
	 */
	public void setFormRole(String formRole) {
		this.formRole = formRole;
	}

	/**
	 * @return the post
	 */
	public Post<? extends Resource> getPost() {
		return this.post;
	}

	/**
	 * @param post the post to set
	 */
	public void setPost(Post<? extends Resource> post) {
		this.post = post;
	}
}
