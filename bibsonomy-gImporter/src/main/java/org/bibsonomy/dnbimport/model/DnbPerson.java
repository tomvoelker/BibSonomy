package org.bibsonomy.dnbimport.model;

/**
 * TODO: add documentation to this class
 * 
 * @author jensi
 */
public class DnbPerson {
	private String personId;
	
	private String firstName;

	private String lastName;

	private String personFunction;
	
	private boolean diffPerson;
	
	private String gender;

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPersonFunction() {
		return this.personFunction;
	}

	public void setPersonFunction(String personFunction) {
		this.personFunction = personFunction;
	}

	public boolean isDiffPerson() {
		return this.diffPerson;
	}

	public void setDiffPerson(boolean diffPerson) {
		this.diffPerson = diffPerson;
	}

	public String getPersonId() {
		return this.personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

}
