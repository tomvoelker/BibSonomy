package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;

/**
 * @author Nils Raabe
 * @version $Id$
 */
public class TaggedFriendResourceViewCommand extends TagResourceViewCommand{

	private String 			requestedUserRelation 	= "";
	private List<User> 		relatedUsers;
	List<Post<Bookmark>> 	bmPosts;
	List<Post<BibTex>> 		bibPosts;
	
	
	/**
	 * @return the requestedUserRelation
	 */
	public String getRequestedUserRelation() {
		return this.requestedUserRelation;
	}
	/**
	 * @param requestedUserRelation the requestedUserRelation to set
	 */
	public void setRequestedUserRelation(String requestedUserRelation) {
		this.requestedUserRelation = requestedUserRelation;
	}
	
	/**
	 * @return the relatedUsers
	 */
	public List<User> getRelatedUsers() {
		return this.relatedUsers;
	}
	/**
	 * @param relatedUsers the relatedUsers to set
	 */
	public void setRelatedUsers(List<User> relatedUsers) {
		this.relatedUsers = relatedUsers;
	}
	/**
	 * @return the bmPosts
	 */
	public List<Post<Bookmark>> getBmPosts() {
		return this.bmPosts;
	}
	/**
	 * @param bmPosts the bmPosts to set
	 */
	public void setBmPosts(List<Post<Bookmark>> bmPosts) {
		this.bmPosts = bmPosts;
	}
	/**
	 * @return the bibPosts
	 */
	public List<Post<BibTex>> getBibPosts() {
		return this.bibPosts;
	}
	/**
	 * @param bibPosts the bibPosts to set
	 */
	public void setBibPosts(List<Post<BibTex>> bibPosts) {
		this.bibPosts = bibPosts;
	}
}