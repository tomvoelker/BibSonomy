package org.bibsonomy.model;

import java.util.List;

/**
 * This class represents a tag.
 */
public class Tag {

	private int id;
	private String name;
	private String stem; // ? what's this
	private int count; // = globalcount ?
	private int usercount;
	private List<Tag> superTags;
	private List<Tag> subTags;
	private List<Post> posts;
	
	public List<Post> getPosts() {
		return this.posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public List<Tag> getSubTags() {
		return this.subTags;
	}

	public void setSubTags(List<Tag> subTags) {
		this.subTags = subTags;
	}

	public List<Tag> getSuperTags() {
		return this.superTags;
	}

	public void setSuperTags(List<Tag> superTags) {
		this.superTags = superTags;
	}

	public int getUsercount() {
		return this.usercount;
	}

	public void setUsercount(int usercount) {
		this.usercount = usercount;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStem() {
		return this.stem;
	}

	public void setStem(String stem) {
		this.stem = stem;
	}

	@Override
	public String toString() {
		return this.id + " '" + this.name + "' '" + this.stem + "' " + this.count;
	}
}