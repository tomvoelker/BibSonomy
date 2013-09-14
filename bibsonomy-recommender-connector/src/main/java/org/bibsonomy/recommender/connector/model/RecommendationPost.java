package org.bibsonomy.recommender.connector.model;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.RecommendationTag;

/**
 * This class wraps a BibSonomy post as the result of a recommendation to
 * allow greedy loading.
 * 
 * @author lukas
 *
 * @param <T>
 */
public class RecommendationPost<T extends Resource> implements RecommendationItem {
	
	private Post<T> post;

	public RecommendationPost(Post<T> post) {
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
		if(post != null) {
			this.post.setContentId(Integer.parseInt(id));
		}
	}

	@Override
	public List<RecommendationTag> getTags() {
		if(this.post != null) {
			ArrayList<RecommendationTag> tags = new ArrayList<RecommendationTag>();
			for(Tag tag : post.getTags()) {
				tags.add(new TagWrapper(tag));
			}
			return tags;
		}
		return new ArrayList<RecommendationTag>();
	}

	@Override
	public void setTags(List<RecommendationTag> tags) {
		if(this.post != null) {
			ArrayList<Tag> bibTags = new ArrayList<Tag>();
			for(RecommendationTag tag : tags) {
				if(tag instanceof TagWrapper) {
					bibTags.add(((TagWrapper) tag).getTag());
				}
			}
		}
	}

	@Override
	public String getTitle() {
		if(this.post != null) {
			if(this.post.getResource() != null) {
				return this.post.getResource().getTitle();
			}
		}
		return "";
	}

	@Override
	public void setTitle(String title) {
		if(this.post != null) {
			if(this.post.getResource() != null) {
				this.post.getResource().setTitle(title);
			}
		}
	}
	
	public Post<T> getPost() {
		return post;
	}
	
	public void setPost(Post<T> post) {
		this.post = post;
	}

}
