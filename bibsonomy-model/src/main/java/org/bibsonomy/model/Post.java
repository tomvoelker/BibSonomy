package org.bibsonomy.model;

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
	private User user;
	private List<Group> groups;
	private List<Tag> tags;
	private String description;
	private long postingDate; // TODO why not use java.util.Date instead?: ok!

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

	public long getPostingDate() {
		return this.postingDate;
	}

	public void setPostingDate(long postingDate) {
		this.postingDate = postingDate;
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
}