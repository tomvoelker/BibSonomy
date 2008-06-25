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
	 * The internal id of this group.
	 */
	private int groupId = GroupID.INVALID.getId();

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
	 * attached to BibTeX posts, if the post is viewable for the group or
	 * public.
	 */
	private boolean sharedDocuments;

	/**
	 * constructor
	 */
	public Group() {
		//this(GroupID.PUBLIC);
	}

	/**
	 * constructor
	 * 
	 * @param name
	 */
	public Group(final String name) {
		this();
		this.setName(name);
	}

	/**
	 * constructor
	 * 
	 * @param groupId
	 */
	public Group(final GroupID groupId) {
		this(groupId.getId());
	}

	/**
	 * constructor
	 * 
	 * @param groupid
	 */
	public Group(final int groupid) {
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
	 * Returns the first member of this group.
	 * 
	 * @see User#getFriend()
	 * @return first user
	 */
	public User getUser() {
		if (this.getUsers().size() < 1) return null;
		return this.users.get(0);
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
	 * attached to BibTeX posts, if the post is viewable for the group or
	 * public.
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
		if (!(obj instanceof Group)) {
			return false;
		}
		return equals((Group) obj);
	}

	/**
	 * Compares two groups. Two groups are equal, if their groupId is equal.
	 * 
	 * @param other
	 * @return <code>true</code> if the two groups are equal.
	 */
	public boolean equals(final Group other) {
		if (this.groupId != GroupID.INVALID.getId() && other.groupId != GroupID.INVALID.getId()) {
			/*
			 * both groups have IDs set --> compare them by id
			 */
			if (this.name != null && other.name != null) {
				/*
				 * since both have also names set ... we should include the names in the comparison!
				 */
				if ((this.groupId == other.groupId && !this.name.equals(other.name)) ||
						(this.groupId != other.groupId &&  this.name.equals(other.name))) {
					/*
					 * IDs do not match with names --> exception! 
					 */
					throw new RuntimeException("The names and the IDs of the given groups " + this + " and " + other + " do not match.");
				}
			}
			return this.groupId == other.groupId;
		} 
		/*
		 * at least one of the groups has no ID set --> check their name
		 */
		if (this.name != null && other.name != null) {
			return this.name.equals(other.name);
		}
		throw new RuntimeException("The given groups " + this + " and " + other + " are incomparable.");
	}

	/** 
	 * Returns a string representation of a group in the form <code>name(groupId)</code>. 
	 *  
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name + "(" + this.groupId + ")";
	}

	@Override
	public int hashCode() {
		return groupId;
	}

}