package org.bibsonomy.model;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Privlevel;

/**
 * A group groups users.
 */
public class Group {

	/**
	 * The internal id of this group.
	 */
	private int groupId;

	/**
	 * This group's name.
	 */
	private String name;

	/**
	 * A short text describing this group.
	 */
	private String description;

	/**
	 * These are the {@link Post}s of this group.
	 */
	private List<Post<? extends Resource>> posts;

	/**
	 * These {@link User}s belong to this group.
	 */
	private List<User> users;

	/**
	 * The privacy level of this group.
	 */
	private Privlevel privlevel;

	public Group() {
		this.groupId = GroupID.PUBLIC.getId();
		this.privlevel = Privlevel.MEMBERS;
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

	public List<Post<? extends Resource>> getPosts() {
		if (this.posts == null) {
			this.posts = new LinkedList<Post<? extends Resource>>();
		}
		return this.posts;
	}

	public void setPosts(List<Post<? extends Resource>> posts) {
		this.posts = posts;
	}

	public List<User> getUsers() {
		if (this.users == null) {
			this.users = new LinkedList<User>();
		}
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public int getPrivlevel() {
		return this.privlevel.getId();
	}

	public void setPrivlevel(Privlevel privlevel) {
		this.privlevel = privlevel;
	}
}