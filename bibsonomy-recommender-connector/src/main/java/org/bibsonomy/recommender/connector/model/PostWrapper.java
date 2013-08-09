package org.bibsonomy.recommender.connector.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

import recommender.core.interfaces.model.RecommendationTag;
import recommender.core.interfaces.model.RecommendationUser;
import recommender.core.interfaces.model.TagRecommendationEntity;

/**
 * This class wraps a bibsonomy post to pass it to the recommender framework
 * 
 * @author Lukas
 *
 * @param <T>
 */

public class PostWrapper <T extends Resource> implements TagRecommendationEntity {

	/**
	 * for serialization
	 */
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
	public Date getDate() {
		if(this.post != null) {
			return this.post.getDate();
		}
		return new Date();
	}

	@Override
	public void setDate(Date date) {
		if(this.post != null) {
			this.post.setDate(date);
		}
	}

	@Override
	public String getId() {
		if(this.post != null) {
			return ""+this.post.getContentId();
		}
		return null;
	}

	@Override
	public void setId(String id) {
		if(this.post != null) {
			this.post.setContentId(Integer.parseInt(id));
		}
	}

	@Override
	public RecommendationUser getUser() {
		if(this.post != null) {
			return new UserWrapper(this.post.getUser());
		}
		return null;
	}

	@Override
	public void setUser(RecommendationUser user) {
		if(this.post != null) {
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

	@Override
	public void setTitle(String title) {
		if(this.post != null) {
			if (this.post.getResource() != null) {
				this.post.getResource().setTitle(title);
			}
		}
	}

	@Override
	public String getUrl() {
		if(this.post != null) {
			if (this.post.getResource() != null &&
					this.post.getResource() instanceof BibTex) {
				return ((BibTex) this.post.getResource()).getUrl();
			}
		}
		return "";
	}

	@Override
	public void setUrl(String url) {
		if(this.post != null) {
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