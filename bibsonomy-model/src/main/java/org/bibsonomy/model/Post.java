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
	private Date date;
    
	/**
	 * The description should be part of the post because it's a description individually made by 
	 * one user for his post - another user may descripe the post with another text 
	 */
	private String description;
	
	/**
	 * only for testCases
	 * its not a good idea to damage the model just to let some unit-tests perform..
	 */
	@Deprecated
	private int groupId;
	
	public Post() {
		this.tags = new ArrayList<Tag>();
	}

	public int getContentId() {
		return this.contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
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

	public void addGroup(final String groupName) {
		final Group group = new Group();
		group.setName(groupName);
		this.groups.add(group);
	}

	
	/**
	 * only for testCases
	 * its not a good idea to damage the model just to let some unit-tests perform..
	 */
	@Deprecated
	public int getGroupId() {
		return this.groupId;
	}
	/**
	 * only for testCases
	 * its not a good idea to damage the model just to let some unit-tests perform..
	 */
	@Deprecated
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}