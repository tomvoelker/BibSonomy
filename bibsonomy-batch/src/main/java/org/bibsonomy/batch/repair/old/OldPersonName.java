package org.bibsonomy.batch.repair.old;

/**
 * The name of a person.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class OldPersonName {
	public static final String LAST_FIRST_DELIMITER = ",";
	private String name;
	private String firstName;
	private String lastName;

	/**
	 * Default constructor
	 */
	public OldPersonName() {
		// nothing to do
	}
	
	/**
	 * Sets name and extracts first and last name.
	 * 
	 * @param name
	 */
	public OldPersonName(final String name) {
		this.setName(name);
	}
	
	/**
	 * @return the firstname(s) of the person
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * @return the lastname(s) of the person
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * @return the full name of the person
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * sets the full name and tries to set first- and lastname from extracted values also. 
	 * @param name the full name of the person
	 */
	public void setName(String name) {
		this.name = name;
		OldPersonNameUtils.discoverFirstAndLastName(name, this);
	}

	/**
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


}
