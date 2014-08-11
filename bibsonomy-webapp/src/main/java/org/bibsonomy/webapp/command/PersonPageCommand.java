package org.bibsonomy.webapp.command;

import org.bibsonomy.model.Person;

/**
 * @author Christian Pfeiffer
 */
public class PersonPageCommand extends UserResourceViewCommand {

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

	private String requestedPersonName;
	private String requestedRole;
	private String requestedPersonId;
	private String requestedHash;
	private String requestedAction;
	
	/**
	 * @return the formSelectedName
	 */
	public String getFormSelectedName() {
		return this.formSelectedName;
	}

	/**
	 * @param formSelectedName the formSelectedName to set
	 */
	public void setFormSelectedName(String formSelectedName) {
		this.formSelectedName = formSelectedName;
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

	private String action;
	private String formSelectedName;
	private String formGraduation;
	
	private String formGivenName;
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

	private String formSurName;

	/**
	 * @return the action
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	private Person person;

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
