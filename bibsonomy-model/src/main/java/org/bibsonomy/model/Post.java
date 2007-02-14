package org.bibsonomy.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;

/**
 * A post connects a given resource with a user and a certain date.
 */
public class Post<T extends Resource> {

	private T resource;
	/** We need this here if we want to use groupBy in iBatis */
	private int contentId;
	private User user;
	private List<Group> groups;
	private List<Tag> tags;
	/** A timestamp for this resource */
	private String description;
	private Date date;

	public Post() {
		this.tags = new ArrayList<Tag>();
	}

	public int getContentId() {
		return this.contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Group> getGroups() {
		if( this.groups == null )
		{
			this.groups = new LinkedList<Group>();
		}
		return this.groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date postingDate) {
		this.date = postingDate;
	}

	public T getResource() {
		return this.resource;
	}

	public void setResource(T resource) {
		this.resource = resource;
	}

	public List<Tag> getTags() {
		if( this.tags == null )
		{
			this.tags = new LinkedList<Tag>();
		}
		return this.tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void addTag(final String tagName) {
		final Tag tag = new Tag();
		tag.setName(tagName);
		this.tags.add(tag);
	}

}