package org.bibsonomy.webapp.command;

import org.bibsonomy.common.enums.UserRelation;
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
	private boolean isFollowerOfUser = false;
	
	/**
	 * defines the similarity measure by which the related users are computed  
	 * (default is folkrank)
	 */
	private String userSimilarity = UserRelation.FOLKRANK.name();
	
	/**
	 * @return the concepts
	 */
	public ConceptsCommand getConcepts() {
		return this.concepts;
	}

	/**
	 * @param concepts the concepts to set
	 */
	public void setConcepts(ConceptsCommand concepts) {
		this.concepts = concepts;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}	

	/**
	 * @return the bookmarkCount
	 */
	public int getBookmarkCount() {
		return this.bookmarkCount;
	}

	/**
	 * @param bookmarkCount the bookmarkCount to set
	 */
	public void setBookmarkCount(final int bookmarkCount) {
		this.bookmarkCount = bookmarkCount;
	}

	/**
	 * @return the bibtexCount
	 */
	public int getBibtexCount() {
		return this.bibtexCount;
	}

	/**
	 * @param bibtexCount the bibtexCount to set
	 */
	public void setBibtexCount(final int bibtexCount) {
		this.bibtexCount = bibtexCount;
	}

	/**
	 * Set user similarity 
	 * @param userSimilarity - a string describing the user similarity
	 */
	public void setUserSimilarity(String userSimilarity) {
		this.userSimilarity = userSimilarity;
	}

	/**
	 * Get user similarity 
	 * @return - the user similarity
	 */
	public String getUserSimilarity() {
		return userSimilarity;
	}

	/**
	 * Get boolean if user is following this user or if not
	 * @return true if user already follows this user and false if not
	 */
	public boolean isFollowerOfUser() {
		return this.isFollowerOfUser;
	}

	/**
	 * Set if user is following this use or if not
	 * @param isFollowerOfUser
	 */
	public void setFollowerOfUser(boolean isFollowerOfUser) {
		this.isFollowerOfUser = isFollowerOfUser;
	}

}