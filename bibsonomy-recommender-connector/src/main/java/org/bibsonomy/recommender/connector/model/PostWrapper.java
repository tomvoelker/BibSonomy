package org.bibsonomy.recommender.connector.model;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.io.Serializable;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Repository;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;


import recommender.core.interfaces.model.RecommendationGroup;
import recommender.core.interfaces.model.RecommendationResource;
import recommender.core.interfaces.model.RecommendationTag;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.interfaces.model.RecommendationUser;


public class PostWrapper<T extends Resource> implements Serializable, TagRecommendationEntity{

	/**
	 * for persistence
	 */
	private static final long serialVersionUID = -8566116081805155722L;
	
	private Post<T> post;
	
	public PostWrapper(Post<T> post) {
		this.post = post;
	}
	
	@Override
	public Date getDate() {
		return this.post.getDate();
	}

	@Override
	public void setDate(Date date) {
		this.post.setDate(date);
	}

	@Override
	public String getDescription() {
		return this.post.getDescription();
	}

	@Override
	public void setDescription(String description) {
		this.post.setDescription(description);
	}

	@Override
	public Set<RecommendationTag> getTags() {
		Set<RecommendationTag> tags = new HashSet<RecommendationTag>();
		for(Tag tag : this.post.getTags()) {
			tags.add(new TagWrapper(tag));
		}
		return tags;
	}

	@Override
	public void setTags(Set<RecommendationTag> tags) {
		Set<Tag> bibTags = new HashSet<Tag>();
		for(RecommendationTag tag : tags) {
			if(tag instanceof TagWrapper) {
				bibTags.add(((TagWrapper) tag).getTag());
			}
		}
		this.post.setTags(bibTags);
	}

	@Override
	public Set<RecommendationGroup> getGroups() {
		Set<RecommendationGroup> groups = new HashSet<RecommendationGroup>();
		for(Group group : this.post.getGroups()) {
			groups.add(new GroupWrapper(group));
		}
		return groups;
	}

	@Override
	public void setGroups(Set<RecommendationGroup> groups) {
		Set<Group> bibGroups = new HashSet<Group>();
		for(RecommendationGroup group : groups) {
			if(group instanceof GroupWrapper) {
				bibGroups.add(((GroupWrapper) group).getGroup());
			}
		}
		this.post.setGroups(bibGroups);
	}

	@Override
	public RecommendationResource getResource() {
		if(this.post.getResource() instanceof BibTex) {
			return new BibTexWrapper((BibTex) this.post.getResource());
		} else if (this.post.getResource() instanceof Bookmark) {
			return new BookmarkWrapper((Bookmark) this.post.getResource());
		}
		return null;
	}

	@Override
	public void setResource(RecommendationResource resource) {
		if(resource instanceof BibTexWrapper && 
				(this.post.getResource() instanceof BibTex || this.post.getResource() == null)) {
			this.post.setResource((T) ((BibTexWrapper) resource).getBibtex());
		} else if(resource instanceof BookmarkWrapper && 
				(this.post.getResource() instanceof Bookmark || this.post.getResource() == null)) {
			this.post.setResource((T) ((BookmarkWrapper) resource).getBookmark());
		}
	}

	@Override
	public Integer getContentId() {
		return this.post.getContentId();
	}

	@Override
	public void setContentId(Integer contentId) {
		this.post.setContentId(contentId);
	}

	@Override
	public RecommendationUser getUser() {
		return new UserWrapper(this.post.getUser());
	}

	@Override
	public void setUser(RecommendationUser user) {
		if(user instanceof UserWrapper) {
			this.post.setUser(((UserWrapper) user).getUser());
		}
	}

	public Post<T> getPost() {
		return post;
	}
	
	public void setPost(Post<T> post) {
		this.post = post;
	}
	
}
