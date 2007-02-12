package org.bibsonomy.model;

import java.util.LinkedList;
import java.util.List;


/**
 * Everything, which can be tagged in BibSonomy, is derived from this class.
 */
public abstract class Resource {

	/** An Id for this resource; by default ConstantID.IDS_UNDEFINED_CONTENT_ID */
	private int contentId;
	private String url = ""; 
	private int count;
	private String oldHash = "";
	private int newTasId;
	private String interHash;
	private String intraHash;
	private List<Post<Resource>> posts;
	

	// XXX: put them only in the model, if we really need them
	// private String group; FIXME was this meant to be groupName or groupId
	// private String title; FIXME belongs to BibTex?
	// private String privnote; FIXME belongs to BibTex?

	
	public abstract String getHash();
	public String getOldHash() {
		return this.oldHash;
	}
	public void setOldHash(Resource resource) {
		this.oldHash = resource.getHash();
	}

	public int getContentId() {
		return this.contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}
	/**
	 * Adds a tag with the given name.
	 * 
	 * @param tag
	 *            Name of the tag.
	 */
	// @Deprecated?
	public int getNewTasId() {
		return this.newTasId;
	}
	// @Deprecated?
	public void setNewTasId(int newTasId) {
		this.newTasId = newTasId;
	}
	public String getInterHash() {
		return this.interHash;
	}
	public void setInterHash(String interHash) {
		this.interHash = interHash;
	}
	public String getIntraHash() {
		return this.intraHash;
	}
	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
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
	public int getCount() {
		return this.count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getUrl() {
		return this.url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}