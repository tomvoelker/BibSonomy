package org.bibsonomy.webapp.command;

import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.User;



/**
 * @author Philipp Beau
 *
 */
public class CvPageCommand extends TagResourceViewCommand {
	
	/** the group whode resources are requested*/
	private ConceptsCommand concepts = new ConceptsCommand();

	/** the intrahash of a publication **/
	private String requBibtex = "";
	private User user;
	private int bookmarkCount = 0;
	private int bibtexCount = 0;
	private boolean isFollowerOfUser = false;
	
	/**
	 * defines the similarity measure by which the related users are computed  
	 * (default is folkrank)
	 */
	private String userSimilarity = UserRelation.FOLKRANK.name();
	

	public void setRequBibtex(String requBibtex) {
		this.requBibtex = requBibtex;
	}

	/**
	 * @return the hash of a bibtex
	 */
	public String getRequBibtex(){
		return this.requBibtex;
	}
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
