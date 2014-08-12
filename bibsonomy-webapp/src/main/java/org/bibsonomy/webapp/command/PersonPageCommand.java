package org.bibsonomy.webapp.command;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;

/**
 * @author Christian Pfeiffer
 */
public class PersonPageCommand extends UserResourceViewCommand {


	private String requestedPersonName;
	private String requestedRole;
	private String requestedPersonId;
	private String requestedHash;
	private String requestedAction;
	
	private PersonName formSelectedName;
	private String formGraduation;
	private String formGivenName;
	private String formSurName;
	
	private Person person;
	
	
	// TODO: inject person
	public PersonPageCommand() {
		this.person = new Person();
	}
	
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
	 * @return the requestedPersonName
	 */
	public String getRequestedPersonName() {
		return this.requestedPersonName;
	}

	/**
	 * @param requestedPersonName the requestedPersonName to set
	 */
	public void setRequestedPersonName(String requestedPersonName) {
		this.requestedPersonName = requestedPersonName;
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
	 * @return
	 */
	public String getRequestedRole() {
		return this.requestedRole;
	}
	
	/**
	 * @param person
	 */
	public void setRequestedPersonId(String personId) {
		this.requestedPersonId = personId;
	}
	
	/**
	 * @param role
	 */
	public void setRequestedRole(String role) {		
		this.requestedRole = role;
	}

	/**
	 * @return
	 */
	public String getRequestedHash() {
		return this.requestedHash;
	}
	
	/**
	 * @param hash
	 */
	public void setRequestedHash(String hash) {
		this.requestedHash = hash;
	}
}
