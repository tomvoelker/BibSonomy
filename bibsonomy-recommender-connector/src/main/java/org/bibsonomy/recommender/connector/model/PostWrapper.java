package org.bibsonomy.recommender.connector.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

import recommender.core.interfaces.model.RecommendationGroup;
import recommender.core.interfaces.model.RecommendationTag;
import recommender.core.interfaces.model.RecommendationUser;
import recommender.core.model.TagRecommendationEntity;

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
		HashSet<RecommendationTag> resultTags = new HashSet<RecommendationTag>();
		for(Tag t : this.post.getTags()) {
			resultTags.add(new TagWrapper(t));
		}
		return resultTags;
	}

	@Override
	public void setTags(Set<RecommendationTag> tags) {
		HashSet<Tag> resultTags = new HashSet<Tag>();
		for(RecommendationTag t : tags) {
			resultTags.add(((TagWrapper) t).getTag());
		}
		this.post.setTags(resultTags);
	}

	@Override
	public Set<RecommendationGroup> getGroups() {
		HashSet<RecommendationGroup> resultGroups = new HashSet<RecommendationGroup>();
		for(Group g : this.post.getGroups()) {
			resultGroups.add(new GroupWrapper(g));
		}
		return resultGroups;
	}

	@Override
	public void setGroups(Set<RecommendationGroup> groups) {
		HashSet<Group> resultGroups = new HashSet<Group>();
		for(RecommendationGroup g : groups) {
			resultGroups.add(((GroupWrapper) g).getGroup());
		}
		this.post.setGroups(resultGroups);
	}

	@Override
	public Integer getID() {
		return this.post.getContentId();
	}

	@Override
	public void setID(Integer id) {
		this.post.setContentId(id);
	}

	@Override
	public RecommendationUser getUser() {
		return new UserWrapper(this.post.getUser());
	}

	@Override
	public void setUser(RecommendationUser user) {
		this.post.setUser(((UserWrapper) user).getUser());
	}

	@Override
	public String getTitle() {
		return this.post.getResource().getTitle();
	}

	@Override
	public void setTitle(String title) {
		this.post.getResource().setTitle(title);
	}

	@Override
	public String getUrl() {
		if (this.post.getResource() instanceof BibTex) {
			return ((BibTex) this.post.getResource()).getUrl();
		}
		return "";
	}

	@Override
	public void setUrl(String url) {
		if (this.post.getResource() instanceof BibTex) {
			((BibTex) this.post.getResource()).setUrl(url);
		}
	}
	
}