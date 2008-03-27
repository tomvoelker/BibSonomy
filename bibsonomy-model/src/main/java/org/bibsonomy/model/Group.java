package org.bibsonomy.model;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Privlevel;

/**
 * A group groups users.
 * 
 * @version $Id$
 */
public class Group {

	/**
	 * The internal id of this group. TODO: shouldn't this be a {@link GroupID}?!
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
	
	/**
	 * If <code>true</code>, other group members can access documents 
	 * attached to BibTeX posts, if the post is viewable for the group
	 * or public.
	 */
	private boolean sharedDocuments;

	/**
	 * constructor
	 */
	public Group() {
		this.groupId = GroupID.PUBLIC.getId();
		this.privlevel = Privlevel.MEMBERS;
		this.sharedDocuments = false;
	}

	/**
	 * constructor
	 * @param groupid 
	 */
	public Group(GroupID groupid) {
		this.groupId = groupid.getId();
		this.privlevel = Privlevel.MEMBERS;
		this.sharedDocuments = false;
	}
	
	/**
	 * constructor
	 * @param groupid
	 */
	public Group(Integer groupid) {
		this.groupId = groupid;
		this.privlevel = Privlevel.MEMBERS;
		this.sharedDocuments = false;		
	}
	
	/**
	 * @return groupId
	 */
	public int getGroupId() {
		return this.groupId;
	}

	/**
	 * @param groupId
	 */
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return posts
	 */
	public List<Post<? extends Resource>> getPosts() {
		if (this.posts == null) {
			this.posts = new LinkedList<Post<? extends Resource>>();
		}
		return this.posts;
	}

	/**
	 * @param posts
	 */
	public void setPosts(List<Post<? extends Resource>> posts) {
		this.posts = posts;
	}

	/**
	 * @return users
	 */
	public List<User> getUsers() {
		if (this.users == null) {
			this.users = new LinkedList<User>();
		}
		return this.users;
	}

	/**
	 * @param users
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	/**
	 * @return privlevel
	 */
	public Privlevel getPrivlevel() {
		return this.privlevel;
	}

	/**
	 * @param privlevel
	 */
	public void setPrivlevel(Privlevel privlevel) {
		this.privlevel = privlevel;
	}

	/**
	 * If <code>true</code>, other group members can access documents 
	 * attached to BibTeX posts, if the post is viewable for the group
	 * or public.
	 * 
	 * @return The truth value regarding shared documents for this group.
	 */
	public boolean isSharedDocuments() {
		return this.sharedDocuments;
	}

	/**
	 * @param sharedDocuments
	 */
	public void setSharedDocuments(boolean sharedDocuments) {
		this.sharedDocuments = sharedDocuments;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (! (obj instanceof Group)) {
			return false;
		}
		return equals((Group) obj);
	}
	
	/** Compares two groups. Two groups are equal, if their groupId is equal.
	 * @param other
	 * @return <code>true</code> if the two groups are equal.
	 */
	public boolean equals (Group other) {
		return this.groupId == other.groupId;
	}
	
	@Override
	public int hashCode() {
		return groupId;
	}
}