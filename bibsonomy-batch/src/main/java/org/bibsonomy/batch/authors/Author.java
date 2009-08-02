package org.bibsonomy.batch.authors;

import java.util.HashSet;
import java.util.Vector;

/**
 * @author claus
 * @version $Id$
 */
public class Author {

	private long authorId;
	private HashSet<Long> contentIds;
	private HashSet<Long> deletedContentIds;
	private String firstName;
	private String middleName;
	private String lastName;
	private Vector<String> bibtexNames;
	private int ctr;
	
	
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
		
		this.bibtexNames = new Vector<String>();
		this.bibtexNames.add(bibtexName);
		
		this.contentIds = new HashSet<Long>();
		this.deletedContentIds = new HashSet<Long>();
	}
	
	public Author() {
		
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
	 * @return Vector<Long> : content ids
	 */
	public HashSet<Long> getContentIds() {
		return this.contentIds;
	}

	
	/**
	 * @param contentId
	 */
	public void setContentIds(final HashSet<Long> contentId) {
		this.contentIds = contentId;
	}
	
	public HashSet<Long> getDeletedContentIds() {
		return deletedContentIds;
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
	public Vector<String> getBibtexNames() {
		return this.bibtexNames;
	}

	
	/**
	 * @param bibtexName
	 */
	public void addBibtexName(final String bibtexName) {
		this.bibtexNames.add(bibtexName);
	}
	
	public int getCtr() {
		return ctr;
	}

	public void setCtr(int ctr) {
		this.ctr = ctr;
	}
	
	public String toString() {
		String s = "id: " + authorId + ", first: " + firstName
				+ ", middleName: " + middleName + ", lastName: "
				+ lastName + "\ncontent ids: ";
		for(long l : contentIds) {
			s += l + ", ";
		}
		s += "\nbibtexNames: ";
		for(String b : bibtexNames) {
			s += b + ", ";
		}
		return s;
		
	}
	
}