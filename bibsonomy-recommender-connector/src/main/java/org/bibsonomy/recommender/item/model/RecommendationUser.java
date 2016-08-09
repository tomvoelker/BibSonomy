package org.bibsonomy.recommender.item.model;

import java.util.Set;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

/**
 * based on this new items will be recommended
 *
 * @author lha, dzo
 */
public class RecommendationUser {
	
	private String userName;
	
	private Set<Tag> tags;
	
	private Post<? extends Resource> posts;
	
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the tags
	 */
	public Set<Tag> getTags() {
		return this.tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * @return the posts
	 */
	public Post<? extends Resource> getPosts() {
		return this.posts;
	}

	/**
	 * @param posts the posts to set
	 */
	public void setPosts(Post<? extends Resource> posts) {
		this.posts = posts;
	}
}
