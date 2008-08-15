package org.bibsonomy.database.params;

import java.util.List;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Parameters that are specific to tags.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @author Christian Kramer
 * @version $Id$
 */
public class TagParam extends GenericParam {

	// FIXME Probably a duplicate: previously newContentId from GenericParam was used
	private int id;
	// FIXME: don't know if it is the third variable with the same meaning, but
	// at least it is the first one, with an intuitive name
	private Integer tasId;
	private String name;
	private String stem;
	private int count;
	private int usercount;
	private HashID hashId = HashID.INTER_HASH;
	private TagSimilarity tagRelationType;

	/**
	 * Decides whether to retrieve the subtags of the current tag
	 */
	private boolean retrieveSubTags;

	/**
	 * Decides whether to retrieve the superTags of the current tag
	 */
	private boolean retrieveSuperTags;

	/**
	 * Decides whether to retrieve the sub-/supertags in a transitive manner,
	 * i.e. the complete subtree under the current tag (subtags) or all tags
	 * from the current tag up to the tree root (supertags)
	 */
	private boolean retrieveSubSuperTagsTransitive;

	/**
	 * TODO document
	 */
	private List<Post<? extends Resource>> posts;

	/**
	 * Regular expression
	 */
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

	public boolean isRetrieveSubSuperTagsTransitive() {
		return this.retrieveSubSuperTagsTransitive;
	}

	public void setRetrieveSubSuperTagsTransitive(boolean retrieveSubSuperTagsTransitive) {
		this.retrieveSubSuperTagsTransitive = retrieveSubSuperTagsTransitive;
	}

	public boolean isRetrieveSubTags() {
		return this.retrieveSubTags;
	}

	public void setRetrieveSubTags(boolean retrieveSubTags) {
		this.retrieveSubTags = retrieveSubTags;
	}

	public boolean isRetrieveSuperTags() {
		return this.retrieveSuperTags;
	}

	public void setRetrieveSuperTags(boolean retrieveSuperTags) {
		this.retrieveSuperTags = retrieveSuperTags;
	}

	public int getHashId() {
		return this.hashId.getId();
	}

	public void setHashId(HashID hashId) {
		this.hashId = hashId;
	}

	public TagSimilarity getTagRelationType() {
		return this.tagRelationType;
	}

	public void setTagRelationType(TagSimilarity tagRelationType) {
		this.tagRelationType = tagRelationType;
	}

}