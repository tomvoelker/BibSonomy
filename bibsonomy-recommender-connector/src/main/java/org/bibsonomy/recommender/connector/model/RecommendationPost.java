package org.bibsonomy.recommender.connector.model;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.RecommendationTag;

/**
 * This class wraps a post as the result of a recommendation to
 * allow greedy loading.
 * 
 * @author lukas
 */
public class RecommendationPost implements RecommendationItem {
	
	private Post<? extends Resource> post;
	
	/**
	 * init the recommendation post
	 * @param post
	 */
	public RecommendationPost(Post<? extends Resource> post) {
		this.post = post;
	}
	
	@Override
	public String getId() {
		if(post != null) {
			return "" + post.getContentId();
		}
		return "";
	}

	@Override
	public void setId(String id) {
		if (post != null) {
			this.post.setContentId(new Integer(id));
		}
	}

	@Override
	public List<RecommendationTag> getTags() {
		final List<RecommendationTag> tags = new ArrayList<RecommendationTag>();
		if (this.post != null) {
			for (Tag tag : post.getTags()) {
				tags.add(new TagWrapper(tag));
			}
		}
		return tags;
	}

	@Override
	public void setTags(List<RecommendationTag> tags) {
		if (this.post != null) {
			final List<Tag> bibTags = new ArrayList<Tag>();
			for (RecommendationTag tag : tags) {
				if (tag instanceof TagWrapper) {
					bibTags.add(((TagWrapper) tag).getTag());
				}
			}
		}
	}

	@Override
	public String getTitle() {
		if (this.post != null) {
			if (this.post.getResource() != null) {
				return this.post.getResource().getTitle();
			}
		}
		return "";
	}

	@Override
	public void setTitle(String title) {
		if (this.post != null) {
			if (this.post.getResource() != null) {
				this.post.getResource().setTitle(title);
			}
		}
	}

	/**
	 * @return the post
	 */
	public Post<? extends Resource> getPost() {
		return this.post;
	}

	/**
	 * @param post the post to set
	 */
	public void setPost(Post<? extends Resource> post) {
		this.post = post;
	}
}
