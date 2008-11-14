package org.bibsonomy.batch.authors;

import java.util.ArrayList;

/**
 * @author nmrd
 * @version $Id$
 */
public class Author {

	private long authorId;
	private ArrayList<Long> contentId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String bibtexName;
	
	
	/**
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @param bibtexName
	 */
	public Author(final String firstName, final String middleName, final String lastName, final String bibtexName) {
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.bibtexName = bibtexName;
		
		contentId = new ArrayList<Long>();
		
	}

	
	/**
	 * @return long : authorid
	 */
	public long getAuthorId() {
		return this.authorId;
	}

	
	/**
	 * @param authorId
	 */
	public void setAuthorId(final long authorId) {
		this.authorId = authorId;
	}

	
	/**
	 * @return ArrayList<Long> : content ids
	 */
	public ArrayList<Long> getContentIds() {
		return this.contentId;
	}

	
	/**
	 * @param contentId
	 */
	public void setContentId(final ArrayList<Long> contentId) {
		this.contentId = contentId;
	}

	
	/**
	 * @return String : firstName
	 */
	public String getFirstName() {
		return this.firstName;
	}

	
	/**
	 * @param firstName
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	
	/**
	 * @return String : middleName
	 */
	public String getMiddleName() {
		return this.middleName;
	}

	
	/**
	 * @param middleName
	 */
	public void setMiddleName(final String middleName) {
		this.middleName = middleName;
	}

	
	/**
	 * @return String : lastName
	 */
	public String getLastName() {
		return this.lastName;
	}

	
	/**
	 * @param lastName
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	
	/**
	 * @return String : bibtexName
	 */
	public String getBibtexName() {
		return this.bibtexName;
	}

	
	/**
	 * @param bibtexName
	 */
	public void setBibtexName(final String bibtexName) {
		this.bibtexName = bibtexName;
	}
	
}