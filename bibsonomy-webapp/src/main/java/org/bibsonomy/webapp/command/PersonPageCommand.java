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
