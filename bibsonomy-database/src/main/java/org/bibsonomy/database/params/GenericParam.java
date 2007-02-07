package org.bibsonomy.database.params;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

/**
 * This is the most generic param. All fields which are not specific to
 * bookmarks or BibTexs are collected here. The parameter-objects are used by
 * iBATIS in the SQL-statements to fill in values; they are put at the position
 * of ?-marks.
 * 
 * @author Christian Schenk
 */
public abstract class GenericParam<T extends Resource> {

	// FIXME
//	/** A single resource */
//	protected T resource;
	/** A list of resources. */
	private List<T> resources;
	/** A list of tags */
    private List<Tag> tags;
    private Tag tag;
	/**
	 * List of (tagname, index)-pairs, where tagname can be both a name of a tag
	 * or concept.
	 */
	private final List<TagIndex> tagIndex;
	/** List of the groups the user belongs to */
	private List<Integer> groups;
	/**
	 * Should tagnames (names of tags and concepts) be case sensitive; by
	 * default this is false, i.e. tagnames aren't case sensitive.
	 */
	private boolean caseSensitiveTagNames;
	private Date date;
	/** If a contentId is updated or deleted we need this as reference */
	private int requestedContentId;
	/** The hash of a post, e.g. a bookmark or a BibTex */
	private String hash;
	/** RegEx search pattern */
	private String search;
	/** This is the current user. */
	private String userName;
	/**
	 * The current user, who would be identified by userName, can look at other
	 * people's content. This requested user is identified by this string.
	 */
	private String requestedUserName;
	/** ID of a group; by default it's invalid */
	private int groupId;
	/** If we're searching for a group this is used for the name of the group */
	private String requestedGroupName;
	/** The type of a group is by default public */
	private ConstantID groupType;
	/** The SQL-Limit which is by default 10 */
	private int limit;
	/** The SQL-Offset which is by default 0 */
	private int offset;
	/** Is user a spammer; by default false */
	private ConstantID spammer;
	/** The type of a ID is by default DS_CONTENT_ID **/
	private ConstantID idsType;
    private int newContentId;
    private int ContendIDbyBookmark;
    private int newTasId;

	public GenericParam() {
		this.tagIndex = new ArrayList<TagIndex>();
		this.caseSensitiveTagNames = false;
		this.groupId = ConstantID.GROUP_INVALID.getId();
		this.groupType = ConstantID.GROUP_PUBLIC;
		this.idsType=ConstantID.IDS_CONTENT_ID;
		this.limit = 10;
		this.offset = 0;
		this.spammer = ConstantID.SPAMMER_FALSE;
	}

	/**
	 * Implementations of this class will have to implement this method to
	 * identify their content type.
	 */
	public abstract int getContentType();

	public boolean isCaseSensitiveTagNames() {
		return this.caseSensitiveTagNames;
	}

	public void setCaseSensitiveTagNames(boolean caseSensitive) {
		this.caseSensitiveTagNames = caseSensitive;
	}

	public void addTagName(final String tagName) {
		this.tagIndex.add(new TagIndex(tagName, this.tagIndex.size() + 1));
	}

	public List<TagIndex> getTagIndex() {
		return this.tagIndex;
	}

	/**
	 * This is used to determine the max. amount of join-indices for the
	 * iteration of the join-index; e.g. if we're searching for tag names. If we
	 * have only one tag, we don't need a join index, if we got two then we need
	 * one, if we got three then we need two, and so on.<br/> We had to
	 * introduce this because iBATIS can only call methods that are true getter
	 * or setter. A call to tagIndex.size() is not possible. An attempt fails
	 * with "There is no READABLE property named 'size' in class
	 * 'java.util.ArrayList'".
	 */
	public int getMaxTagIndex() {
		return this.tagIndex.size();
	}

	public int getGroupType() {
		return groupType.getId();
	}

	public void setGroupType(ConstantID groupType) {
		this.groupType = groupType;
	}

	public String getSearch() {
		return this.search;
	}

	// TODO write testcase
	public void setSearch(String search) {
		this.search = search.replaceAll("([\\s]|^)([\\S&&[^-]])", " +$2");
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String user) {
		this.userName = user;
	}

	public int getLimit() {
		return this.limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return this.offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Integer> getGroups() {
		return this.groups;
	}

	public void setGroups(List<Integer> groups) {
		this.groups = groups;
	}

	public int getGroupId() {
		return this.groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getHash() {
		return this.hash;
	}

	public void setHash(String requBibtex) {
		this.hash = requBibtex;
	}

	public String getRequestedUserName() {
		return this.requestedUserName;
	}

	public void setRequestedUserName(String requestedUserName) {
		this.requestedUserName = requestedUserName;
	}

	public String getRequestedGroupName() {
		return this.requestedGroupName;
	}

	public void setRequestedGroupName(String requestedGroupName) {
		this.requestedGroupName = requestedGroupName;
	}

	public int getSpammer() {
		return this.spammer.getId();
	}

	public void setSpammer(ConstantID spammer) {
		this.spammer = spammer;
	}

	public int getRequestedContentId() {
		return this.requestedContentId;
	}

	public void setRequestedContentId(int requestedContentId) {
		this.requestedContentId = requestedContentId;
	}

	public int getIdsType() {
		return this.idsType.getId();
	}

	public void setIdsType(ConstantID idsType) {
		this.idsType = idsType;
	}

	public int getNewContentId() {
		return this.newContentId;
	}

	public void setNewContentId(int newContentId) {
		this.newContentId = newContentId;
	}

	public int getContendIDbyBookmark() {
		return this.ContendIDbyBookmark;
	}

	public void setContendIDbyBookmark(int contendIDbyBookmark) {
		this.ContendIDbyBookmark = contendIDbyBookmark;
	}

	public List<T> getResources() {
		return this.resources;
	}

	public void setResources(List<T> resources) {
		this.resources = resources;
	}

	// FIXME
//	public abstract T getResource();
//
//	public void setResource(T resource) {
//		this.resource = resource;
//	}

	public List<Tag> getTags() {
		return this.tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public int getNewTasId() {
		return this.newTasId;
	}

	public void setNewTasId(int newTasId) {
		this.newTasId = newTasId;
	}

	public Tag getTag() {
		return this.tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
}