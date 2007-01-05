package org.bibsonomy.ibatis.params;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.beans.TagIndex;

/**
 * This is the most generic param. All fields which are not specific to
 * bookmarks or BibTexs are collected here. The parameter-objects are used by
 * iBATIS in the SQL-statements to fill in values; they are put at the position
 * of ?-marks.
 * 
 * @author Christian Schenk
 */
public abstract class GenericParam {

	/** List of (tagname, index)-pairs */
	private final List<TagIndex> tagIndex;
	/** List of the groups the user belongs to */
	private List<Integer> groups;
	/** Should tagnames be case sensitive */
	private boolean caseSensitiveTagNames;
	private Date date;
	/** The hash of a post */
	private String hash;
	/** RegEx search pattern */
	private String search;
	private String userName;
	/**
	 * The current user, who would be identified by userName, can look at others
	 * people content. This requested user is identified by this.
	 */
	private String requestedUserName;
	private String friendUserName;
	/** ID of a group; by default it's invalid */
	private int groupId;
	/** The type of a group is by default public */
	private ConstantID groupType;
	private int limit;
	private int offset;

	public GenericParam() {
		this.tagIndex = new ArrayList<TagIndex>();
		this.caseSensitiveTagNames = false;
		this.groupId = ConstantID.GROUP_INVALID.getId();
		this.groupType = ConstantID.GROUP_PUBLIC;
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

	public String getFriendUserName() {
		return this.friendUserName;
	}

	public void setFriendUserName(String friendUserName) {
		this.friendUserName = friendUserName;
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
}