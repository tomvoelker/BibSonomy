package org.bibsonomy.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.model.Tag;

/**
 * Everything, which can be tagged in BibSonomy, is derived from this class.
 */
public abstract class Resource {

	/** An Id for this resource; by default ConstantID.IDS_UNDEFINED_CONTENT_ID */
	private int contentId;
	/** The userName who tagged this resource */
	@Deprecated
	private String userName; // FIXME belongs to Post?
	@Deprecated
	private String groupName; // FIXME belongs to Post?
	/** The groupId of this resource; by default ConstantID.GROUP_PUBLIC */
	@Deprecated
	private int groupId; // FIXME belongs to Post?
	/** A timestamp for this resource */
	@Deprecated
	private Date date; // FIXME belongs to Post?
	@Deprecated
	private String url = ""; // FIXME belongs to Bookmark?
	@Deprecated
	private int count;
	@Deprecated
	private String oldHash = "";
	@Deprecated
	private List<Tag> tags; // FIXME belongs to Post?
	private int newTasId; // @Deprecated ?
	@Deprecated
	private boolean spammer; // FIXME belongs to User?
	
	private String interHash;
	private String intraHash;
	private List<Post> posts;
	

	// XXX: put them only in the model, if we really need them
	// private String group; FIXME was this meant to be groupName or groupId
	// private String title; FIXME belongs to BibTex?
	// private String privnote; FIXME belongs to BibTex?

	public Resource() {
		this.contentId = ConstantID.IDS_UNDEFINED_CONTENT_ID.getId();
		this.groupId = ConstantID.GROUP_PUBLIC.getId();
		this.tags = new ArrayList<Tag>();
		// this.group = "public";
		// this.title = "";
	}
	@Deprecated
	public boolean isSpammer() {
		return this.spammer;
	}
	@Deprecated
	public void setSpammer(boolean spammer) {
		this.spammer = spammer;
	}

	public abstract String getHash();
	@Deprecated
	public String getOldHash() {
		return this.oldHash;
	}
	@Deprecated
	public void setOldHash(Resource resource) {
		this.oldHash = resource.getHash();
	}

	public int getContentId() {
		return this.contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}
	@Deprecated
	public String getUserName() {
		return this.userName;
	}
	@Deprecated
	public void setUserName(String userName) {
		this.userName = userName;
	}
	@Deprecated
	public String getGroupName() {
		return this.groupName;
	}
	@Deprecated
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	@Deprecated
	public int getGroupId() {
		return this.groupId;
	}
	@Deprecated
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	@Deprecated
	public Date getDate() {
		return this.date;
	}
	@Deprecated
	public void setDate(Date date) {
		this.date = date;
	}
	@Deprecated
	public String getUrl() {
		return this.url;
	}
	@Deprecated
	public void setUrl(String url) {
		this.url = url;
	}
	@Deprecated
	public int getCount() {
		return this.count;
	}
	@Deprecated
	public void setCount(int count) {
		this.count = count;
	}
	@Deprecated
	public List<Tag> getTags() {
		return this.tags;
	}
	@Deprecated
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * Adds a tag with the given name.
	 * 
	 * @param tag
	 *            Name of the tag.
	 */
	public void addTag(final String tagName) {
		final Tag tag = new Tag();
		tag.setName(tagName);
		this.tags.add(tag);
	}
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
	public List<Post> getPosts() {
		return this.posts;
	}
	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}
}