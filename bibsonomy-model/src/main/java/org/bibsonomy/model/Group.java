package org.bibsonomy.model;

import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;

/**
 * A group groups users.
 */
public class Group {

	private String name;
	private String description;
	private List<Post> posts;
	private List<User> users;

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

	public List<Post> getPosts() {
		return this.posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
}