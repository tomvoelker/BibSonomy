package org.bibsonomy.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A post connects a given resource with a user and a certain date.
 * 
 * @version $Id$
 * @param <T>
 *            resouce type
 */
public class Post<T extends Resource> {

	/**
	 * This is the {@link Resource} that this post is encapsulating.
	 */
	private T resource;

	/**
	 * We need this here if we want to use groupBy in iBatis
	 * TODO: document me
	 * TODO: Is this field really part of the model?
	 */
	private Integer contentId;

	/**
	 * This post belongs to this {@link User}.
	 */
	private User user;

	/**
	 * This post belongs to these {@link Group}s.
	 */
	private Set<Group> groups;

	/**
	 * This post is tagged with these {@link Tag}s.
	 */
	private Set<Tag> tags;

	/**
	 * This is the {@link Date} when this post was lastly modified.
	 */
	private Date date;

	/**
	 * This is a text describing the post. <br/>
	 * 
	 * The description should be part of the post because it's a description
	 * individually made by one user for his post - another user may describe
	 * the post with another text.
	 */
	private String description;

	/**
	 * @return contentId
	 */
	public Integer getContentId() {
		return this.contentId;
	}

	/**
	 * @param contentId
	 */
	public void setContentId(Integer contentId) {
		this.contentId = contentId;
	}

	/**
	 * @return groups
	 */
	public Set<Group> getGroups() {
		if (this.groups == null) {
			this.groups = new HashSet<Group>();
		}
		return this.groups;
	}

	/**
	 * @param groups
	 */
	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	/**
	 * @return postingDate
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * @param postingDate
	 */
	public void setDate(Date postingDate) {
		this.date = postingDate;
	}

	/**
	 * @return resource
	 */
	public T getResource() {
		return this.resource;
	}

	/**
	 * @param resource
	 */
	public void setResource(T resource) {
		this.resource = resource;
	}

	/**
	 * @return tags
	 */
	public Set<Tag> getTags() {
		if (this.tags == null) {
			this.tags = new HashSet<Tag>();
		}
		return this.tags;
	}

	/**
	 * @param tags
	 */
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * @return user
	 */
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
	 * Convenience method to add a tag.
	 * 
	 * @param tagName
	 */
	public void addTag(final String tagName) {
		// call getTags to initialize this.tags
		this.getTags();
		// add the tag
		this.tags.add(new Tag(tagName));
	}

	/**
	 * Convenience method to add a group.
	 * 
	 * @param groupName
	 */
	public void addGroup(final String groupName) {
		// call getGroups to initialize this.groups
		this.getGroups();
		// add the group
		this.groups.add(new Group(groupName));
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}