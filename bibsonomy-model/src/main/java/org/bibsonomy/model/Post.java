package org.bibsonomy.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
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
	private String userName;
	private int groupId;
	private List<Group> groups;
	private String groupName; 
	/** The groupId of this resource; by default ConstantID.GROUP_PUBLIC */
	/** A timestamp for this resource */
	//private Date date;
	private List<Tag> tags;
	private String description;
	private long postingDate; // TODO why not use java.util.Date instead?: ok!

	public Post() {
		this.groupId = ConstantID.GROUP_PUBLIC.getId();
		this.tags = new ArrayList<Tag>();
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

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void addTag(final String tagName) {
		final Tag tag = new Tag();
		tag.setName(tagName);
		this.tags.add(tag);
	}

	public int getGroupId() {
		return this.groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
}