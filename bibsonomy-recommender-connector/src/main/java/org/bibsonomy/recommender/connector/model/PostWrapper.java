package org.bibsonomy.recommender.connector.model;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

import recommender.core.interfaces.model.RecommendationUser;
import recommender.core.interfaces.model.TagRecommendationEntity;

/**
 * This class wraps a BibSonomy post to pass it to the recommender framework.
 * 
 * @author Lukas
 *
 * @param <T>
 */

public class PostWrapper <T extends Resource> implements TagRecommendationEntity {
	/** for serialization */
	private static final long serialVersionUID = -8716576273787930613L;
	
	private Post<T> post;

	public PostWrapper(Post<T> post) {
		this.post = post;
	}
	
	public Post<T> getPost() {
		return post;
	}
	
	public void setPost(Post<T> post) {
		this.post = post;
	}

	@Override
	public String getId() {
		if (this.post != null) {
			return String.valueOf(this.post.getContentId());
		}
		return null;
	}

	@Override
	public void setId(String id) {
		if(this.post != null) {
			this.post.setContentId(new Integer(id));
		}
	}

	@Override
	public RecommendationUser getUser() {
		if(this.post != null) {
			return new UserWrapper(this.post.getUser());
		}
		return null;
	}
	
	/**
	 * @param user	the user to set
	 */
	public void setUser(RecommendationUser user) {
		if (this.post != null) {
			this.post.setUser(((UserWrapper) user).getUser());
		}
	}

	@Override
	public String getTitle() {
		if(this.post != null) {
			if (this.post.getResource() != null) {
				return this.post.getResource().getTitle();
			}
		}
		return "";
	}

	public void setTitle(String title) {
		if(this.post != null) {
			if (this.post.getResource() != null) {
				this.post.getResource().setTitle(title);
			}
		}
	}

	@Override
	public String getUrl() {
		if (this.post != null) {
			if (this.post.getResource() != null &&
					this.post.getResource() instanceof BibTex) {
				return ((BibTex) this.post.getResource()).getUrl();
			}
		}
		return "";
	}

	public void setUrl(String url) {
		if (this.post != null) {
			if (this.post.getResource() != null && 
					this.post.getResource() instanceof BibTex) {
				((BibTex) this.post.getResource()).setUrl(url);
			}
		}
	}

	@Override
	public String getUserName() {
		if(this.post != null && this.post.getUser() != null) {
			return this.post.getUser().getName();
		}
		return "";
	}
}