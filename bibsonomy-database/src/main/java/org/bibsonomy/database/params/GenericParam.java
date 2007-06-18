package org.bibsonomy.database.params;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.Order;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

/**
 * This is the most generic param. All fields which are not specific to
 * bookmarks or BibTexs are collected here. The parameter-objects are used by
 * iBATIS in the SQL-statements to fill in values; they are put at the position
 * of ?-marks.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public abstract class GenericParam {
	/** A list of tags */
	private List<Tag> tags;
	private Tag tag;
	private String tagName;
	private String title;
	/**
	 * List of (tagname, index)-pairs, where tagname can be both a name of a tag
	 * or concept.
	 */
	private final List<TagIndex> tagIndex;
	private int numTransitiveConcepts;
	private int numSimpleConcepts;
	private int numSimpleTags;
	
	/** List of the groups the user belongs to */
	private List<Integer> groups;
	/**
	 * Should tagnames (names of tags and concepts) be case sensitive; by
	 * default this is false, i.e. tagnames aren't case sensitive.
	 */
	private boolean caseSensitiveTagNames;
	/** creation-date */
	private Date date;
	/** If a contentId is updated or deleted we need this as reference */
	private int requestedContentId;
	/**
	 * The hash of a post, e.g. a bookmark or a BibTex TODO: really of the post
	 * and not of the resource? and for what kind of hash is this used? isn't it
	 * resource-specific and shouldn't it be set in the resource-field?
	 */
	private String hash;
	/** RegEx search pattern */
	private String search;
	/** This is the current user. */
	private String userName;
	private String description;
	private String extension;
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
	private GroupID groupType;
	/** The SQL-Limit which is by default 10 */
	private int limit;
	/** The SQL-Offset which is by default 0 */
	private int offset;
	/** Is user a spammer; by default false */
	private ConstantID spammer;
	/*is a user a friend of person x, true will be true*/
	private  boolean friendOf;
	/** The type of a ID is by default DS_CONTENT_ID * */
	private ConstantID idsType;
	private int newContentId;
	// FIXME does this belong into BookmarkParam?
	private int contendIDbyBookmark;
	private String url;
	private ConstantID contentType;
	
	private Order order;
	private GroupingEntity grouping;

	public GenericParam() {
		this.tagIndex = new ArrayList<TagIndex>();
		this.numSimpleTags = 0;
		this.numSimpleConcepts = 0;
		this.numTransitiveConcepts = 0;
		this.caseSensitiveTagNames = false;
		this.groupId = GroupID.INVALID.getId();
		this.groupType = GroupID.PUBLIC;
		this.idsType = ConstantID.IDS_UNDEFINED_CONTENT_ID;
		this.limit = 10;
		this.offset = 0;
		this.spammer = ConstantID.SPAMMER_FALSE;
		this.friendOf=false;
		
		this.grouping = GroupingEntity.ALL;
	}

	/**
	 * Implementations of this class will have to implement this method to
	 * identify their content type.
	 */
	public boolean isCaseSensitiveTagNames() {
		return this.caseSensitiveTagNames;
	}

	public void setCaseSensitiveTagNames(boolean caseSensitive) {
		this.caseSensitiveTagNames = caseSensitive;
	}
	
	private void addToTagIndex(final String tagName) {
		this.tagIndex.add(new TagIndex(tagName, this.tagIndex.size() + 1));
	}

	public void addTagName(final String tagName) {
		this.addToTagIndex(tagName);
		this.numSimpleTags++;
	}
	
	public void addSimpleConceptName(final String tagName) {
		this.addToTagIndex(tagName);
		this.numSimpleConcepts++;
	}
	
	public void addTransitiveConceptName(final String tagName) {
		this.addToTagIndex(tagName);
		this.numTransitiveConcepts++;
	}

	public List<TagIndex> getTagIndex() {
		return Collections.unmodifiableList(this.tagIndex);
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
		// TODO: if this methods name was intuitive, size-1 should be returned
		// because tagIndex[size] is out of bounds
		return this.tagIndex.size();
	}

	public int getGroupType() {
		return groupType.getId();
	}

	public void setGroupType(GroupID groupType) {
		this.groupType = groupType;
	}

	public String getSearch() {
		return this.search;
	}

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

	// TODO: what hash?, what for?, why in genericparam and not in
	// resource-field?
	public String getHash() {
		return this.hash;
	}

	public void setHash(String requBibtex) {
		this.hash = requBibtex;
	}

	// TODO: why in genericparam and not in resource-field?
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
		return this.contendIDbyBookmark;
	}

	public void setContendIDbyBookmark(int contendIDbyBookmark) {
		this.contendIDbyBookmark = contendIDbyBookmark;
	}

	public List<Tag> getTags() {
		return this.tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public Tag getTag() {
		return this.tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
		this.tagName = tag.getName();
	}

	public boolean getFriendOf() {
		return this.friendOf;
	}

	public void setFriendOf(boolean friendOf) {
		this.friendOf = friendOf;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExtension() {
		return this.extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getContentType() {
		return this.contentType.getId();
	}

	public void setContentType(ConstantID contentType) {
		this.contentType = contentType;
	}
	
	public void setContentTypeByClass(Class<? extends Resource> nativeContentType) {
		if (BibTex.class.isAssignableFrom(nativeContentType)) {
			setContentType(ConstantID.BIBTEX_CONTENT_TYPE);
		} else if (Bookmark.class.isAssignableFrom(nativeContentType)) {
			setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
		} else {
			throw new UnsupportedResourceTypeException( nativeContentType.getName() );
		}
	}

	public String getTagName() {
		if (tag != null) {
			return tag.getName();
		}
		return this.tagName;
	}

	public void setTagName(String tagName) {
		this.tag = null;
		this.tagName = tagName;
	}

	public String getTagNameLower() {
		return this.getTagName().toLowerCase();
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public GroupingEntity getGrouping() {
		return this.grouping;
	}

	public void setGrouping(GroupingEntity grouping) {
		this.grouping = grouping;
	}

	public Order getOrder() {
		return this.order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Integer getNumSimpleConcepts() {
		return this.numSimpleConcepts;
	}

	public Integer getNumSimpleTags() {
		return this.numSimpleTags;
	}

	public Integer getNumTransitiveConcepts() {
		return this.numTransitiveConcepts;
	}
}