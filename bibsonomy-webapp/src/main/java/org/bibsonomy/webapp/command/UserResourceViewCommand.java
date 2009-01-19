package org.bibsonomy.webapp.command;

import org.bibsonomy.model.User;

/**
 * Bean for User-Sites
 *
 * @author  Dominik Benz
 * @version $Id$
 */
public class UserResourceViewCommand extends TagResourceViewCommand {

	/** the group whode resources are requested*/
	private ConceptsCommand concepts = new ConceptsCommand();
	/**
     * used to show infos about the user in the sidebar (only for admins, currently)
     */
	private User user;
	private int bookmarkCount = 0;
	private int bibtexCount = 0;
	/**
	 * @return
	 */
	public ConceptsCommand getConcepts() {
		return this.concepts;
	}

	/**
	 * @param concepts
	 */
	public void setConcepts(ConceptsCommand concepts) {
		this.concepts = concepts;
	}

	public User getUser() {
		return this.user;
	}

	/**
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}	

	/**
	 * @return
	 */
	public int getBookmarkCount() {
		return this.bookmarkCount;
	}

	/**
	 * @param bookmarkCount
	 */
	public void setBookmarkCount(final int bookmarkCount) {
		this.bookmarkCount = bookmarkCount;
	}

	/**
	 * @return
	 */
	public int getBibtexCount() {
		return this.bibtexCount;
	}

	public void setBibtexCount(final int bibtexCount) {
		this.bibtexCount = bibtexCount;
	}	
	

}