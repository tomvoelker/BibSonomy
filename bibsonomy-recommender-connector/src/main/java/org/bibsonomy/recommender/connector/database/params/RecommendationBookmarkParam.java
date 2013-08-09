package org.bibsonomy.recommender.connector.database.params;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.recommender.connector.model.ResourceWrapper;

public class RecommendationBookmarkParam {

	private String hash;
	private String title;
	private int group;
	private String username;
	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}
	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the group
	 */
	public int getGroup() {
		return group;
	}
	/**
	 * @param group the group to set
	 */
	public void setGroup(int group) {
		this.group = group;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return a post created with the database information
	 */
	public ResourceWrapper getCorrespondingRecommendationItem() {
		
		Bookmark book = new Bookmark();
		
		book.setInterHash(hash);
		book.setTitle(title);
		
		ResourceWrapper wrapper = new ResourceWrapper(book);
		wrapper.setId(hash + "-" + username);
		
		return wrapper;
		
	}
	
}
