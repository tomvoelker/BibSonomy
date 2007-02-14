package org.bibsonomy.model;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;

/**
 * A group groups users.
 */
public class Group {

	private String name;
	private String description;
	private List<Post<Resource>> posts;
	private List<User> users;
	private int groupId; 
	
	public Group() {
		this.groupId = ConstantID.GROUP_PUBLIC.getId();
	}
	
	
	public int getGroupId() {
		return this.groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Post<Resource>> getPosts() {
		if( this.posts == null )
		{
			this.posts = new LinkedList<Post<Resource>>();
		}
		return this.posts;
	}

	public void setPosts(List<Post<Resource>> posts) {
		this.posts = posts;
	}

	public List<User> getUsers() {
		if( this.users == null )
		{
			this.users = new LinkedList<User>();
		}
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
}