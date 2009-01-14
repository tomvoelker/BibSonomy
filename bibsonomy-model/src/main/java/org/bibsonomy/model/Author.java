package org.bibsonomy.model;

/**
 * @author Christian Claus
 * @version $Id$
 */
public class Author {

	private String authorId = null;
	private String firstName = null;
	private String middle = null;
	private String lastName = null;
	private int ctr = 0;
	
	/**
	 * 
	 * @return
	 */
	public String getAuthorId() {
		return this.authorId;
	}

	/**
	 * 
	 * @param authorId
	 */
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	/**
	 * 
	 * @return
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * 
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * 
	 * @return
	 */
	public String getMiddle() {
		return this.middle;
	}

	/**
	 * 
	 * @param middle
	 */
	public void setMiddle(String middle) {
		this.middle = middle;
	}

	/**
	 * 
	 * @return
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * 
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * 
	 * @return
	 */
	public int getCtr() {
		return this.ctr;
	}

	/**
	 * 
	 * @param ctr
	 */
	public void setCtr(int ctr) {
		this.ctr = ctr;
	}

}
