package org.bibsonomy.model;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a tag.
 * 
 * @version $Id$
 */
public class Tag {

	/**
	 * The id of this tag.
	 */
	private int id;
	
	/**
	 * The name of this tag.
	 */
	private String name;
	
	/**
	 *  FIXME what's this ?
	 */
	private String stem;
	
	/**
	 * Indicating how often this tag is used in the complete system.
	 */
	private int globalcount;
	
	/**
	 * Indicating how often this tag is used by the user.
	 */
	private int usercount;
	
	/**
	 * These are the supertags of this tag:
	 * 
	 * <pre>
	 *   football--&gt; =&gt; football, sports 
	 * </pre>
	 */
	private List<Tag> superTags;
	
	/**
	 * These are the subtags of this tag.
	 * 
	 * <pre>
	 *   --&gt;football =&gt; football, european-football, american-football 
	 * </pre>
	 */
	private List<Tag> subTags;
	
	/**
	 * These are the {@link Post}s that are tagged with this tag.
	 */
	private List<Post<? extends Resource>> posts;

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
	 * @return subTags
	 */
	public List<Tag> getSubTags() {
		if (this.subTags == null) {
			this.subTags = new LinkedList<Tag>();
		}
		return this.subTags;
	}

	/**
	 * @param subTags
	 */
	public void setSubTags(List<Tag> subTags) {
		this.subTags = subTags;
	}

	/**
	 * @return superTags
	 */
	public List<Tag> getSuperTags() {
		if (this.subTags == null) {
			this.subTags = new LinkedList<Tag>();
		}
		return this.superTags;
	}

	/**
	 * @param superTags
	 */
	public void setSuperTags(List<Tag> superTags) {
		this.superTags = superTags;
	}

	/**
	 * @return usercount
	 */
	public int getUsercount() {
		return this.usercount;
	}

	/**
	 * @param usercount
	 */
	public void setUsercount(int usercount) {
		this.usercount = usercount;
	}

	/**
	 * @return id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return count
	 */
	public int getGlobalcount() {
		return this.globalcount;
	}

	/**
	 * @param count
	 */
	public void setGlobalcount(int count) {
		this.globalcount = count;
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
	 * @return stem
	 */
	public String getStem() {
		return this.stem;
	}

	/**
	 * @param stem
	 */
	public void setStem(String stem) {
		this.stem = stem;
	}

	@Override
	public String toString() {
		return this.id + " '" + this.name + "' '" + this.stem + "' " + this.globalcount;
	}
	
	/**
	 * A little helper function to add a list of subtags 
	 * as a space-separated string
	 * @param subtags
	 */
	public void setSubtagsString(String subtags) {
		String [] st = subtags.split(" ");
		for (String subtag : st) {
			Tag s = new Tag();
			s.setName(subtag);
			this.getSubTags().add(s);
		}
	}
}