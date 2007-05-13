package org.bibsonomy.database.params;

import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Parameters that are specific to tags.
 *
 * @author Miranda Grahl
 * @version $Id$
 */
public class TagParam extends GenericParam {

	// FIXME Probably a duplicate: previously newContentId from GenericParam was used
	private int id;
	// FIXME: don't know if it is the third variable with the same meaning, but at least it is the first one, with an intuitive name
	private Integer tasId;
	private String name;
	private String stem;
	private int count;
	private int usercount;

	/*
	 * for request wether to retrieve set of subTags or superTags
	 * decide if relation is transitive or not
	 */
	boolean subTags;
	boolean supertags;
	boolean transitive;
	
	private List<Post<? extends Resource>> posts;
	private String regex;

	public String getRegex() {
		return this.regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
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

	public List<Post<? extends Resource>> getPosts() {
		return this.posts;
	}

	public void setPosts(List<Post<? extends Resource>> posts) {
		this.posts = posts;
	}

	public String getStem() {
		return this.stem;
	}

	public void setStem(String stem) {
		this.stem = stem;
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

	public Integer getTasId() {
		return this.tasId;
	}

	public void setTasId(Integer tasId) {
		this.tasId = tasId;
	}

	public boolean isSubTags() {
		return this.subTags;
	}

	public void setSubTags(boolean subTags) {
		this.subTags = subTags;
	}

	public boolean isSupertags() {
		return this.supertags;
	}

	public void setSupertags(boolean supertags) {
		this.supertags = supertags;
	}

	public boolean isTransitive() {
		return this.transitive;
	}

	public void setTransitive(boolean transitive) {
		this.transitive = transitive;
	}
}