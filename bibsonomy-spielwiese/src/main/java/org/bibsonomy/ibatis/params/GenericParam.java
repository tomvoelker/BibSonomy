package org.bibsonomy.ibatis.params;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.beans.TagIndex;

/**
 * This is the most generic param. All fields which are not specific to
 * bookmarks or BibTexs are collected here.
 * 
 * @author Christian Schenk
 */
public abstract class GenericParam {

	/** List of (tagname, index)-pairs */
	private final List<TagIndex> tagIndex;
	/** Should tagnames be case sensitive */
	private boolean caseSensitiveTagNames;
	private Date date;
	private String search;
	private String userName;
	private Date registrationDate;
	/** By default it's public */
	private ConstantID groupType;
	private ConstantID simHash;
	// FIXME are requSim and simHash the same ???
	private ConstantID requSim;
	private int limit;
	private int offset;

	public GenericParam() {
		this.tagIndex = new ArrayList<TagIndex>();
		this.caseSensitiveTagNames = false;
		this.groupType = ConstantID.GROUP_PUBLIC;
		this.simHash = ConstantID.SIM_HASH;
		this.requSim = ConstantID.SIM_HASH;
	}

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
		this.search = search.replaceAll("([\\s]|^)([\\S&&[^-]])"," +$2");
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

	public int getSimHash() {
		return this.simHash.getId();
	}

	public void setSimHash(ConstantID simHash) {
		this.simHash = simHash;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getRegistrationDate() {
		return this.registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public int getRequSim() {
		return this.requSim.getId();
	}

	public void setRequSim(ConstantID requSim) {
		this.requSim = requSim;
	}
}