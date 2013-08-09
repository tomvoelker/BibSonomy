package org.bibsonomy.batch.authors;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.bibsonomy.model.PersonName;

/**
 * @author claus
 * @version $Id$
 */
public class Author extends PersonName {
	private static final long serialVersionUID = -5159236173916219000L;
	
	
	private long authorId;
	private Set<Long> contentIds;
	private Set<Long> deletedContentIds;
	private String middleName;
	private Vector<String> bibtexNames;
	private int ctr;
	
	
	/**
	 * default constructor
	 */
	public Author() {
		// noop
	}
	
	/**
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @param bibtexName
	 */
	public Author(final String firstName, final String middleName, final String lastName, final String bibtexName) {
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.middleName = middleName;
		
		this.bibtexNames = new Vector<String>();
		this.bibtexNames.add(bibtexName);
		
		this.contentIds = new HashSet<Long>();
		this.deletedContentIds = new HashSet<Long>();
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
	 * @return Set<Long> : content ids
	 */
	public Set<Long> getContentIds() {
		return this.contentIds;
	}
	
	/**
	 * @param contentId
	 */
	public void setContentIds(final HashSet<Long> contentId) {
		this.contentIds = contentId;
	}
	
	/**
	 * @return the deletedContentIds
	 */
	public Set<Long> getDeletedContentIds() {
		return deletedContentIds;
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
	
	/**
	 * @return the ctr
	 */
	public int getCtr() {
		return ctr;
	}
	
	/**
	 * @param ctr the ctr to set
	 */
	public void setCtr(int ctr) {
		this.ctr = ctr;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		s.append("id: ").append(authorId);
		s.append(", first: ").append(this.getFirstName());
		s.append(", middleName: ").append(middleName);
		s.append(", lastName: ").append(this.getLastName());
		s.append("\ncontent ids: ");
		for (long l : contentIds) {
			s.append(l).append(", ");
		}
		s.append("\nbibtexNames: ");
		for (String b : bibtexNames) {
			s.append(b).append(", ");
		}
		return s.toString();
	}
}