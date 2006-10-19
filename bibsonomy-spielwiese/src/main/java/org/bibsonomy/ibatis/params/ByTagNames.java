package org.bibsonomy.ibatis.params;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.ibatis.enums.ContentType;
import org.bibsonomy.ibatis.enums.GroupType;
import org.bibsonomy.ibatis.params.beans.TagIndex;

/**
 * Can be used to search tags by its name.
 * 
 * @author Christian Schenk
 */
public class ByTagNames {

	/** List of (tagname, index)-pairs */
	private final List<TagIndex> tagIndex;
	private ContentType contentType;
	private GroupType groupType;
	private int limit;
	private int offset;
	/** By default it's not case sensitive */
	private boolean caseSensitive;

	public ByTagNames() {
		this.tagIndex = new ArrayList<TagIndex>();
		this.caseSensitive = false;
	}

	public int getContentType() {
		return this.contentType.getId();
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public int getGroupType() {
		return this.groupType.getId();
	}

	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
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

	public boolean isCaseSensitive() {
		return this.caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public void addTagName(final String tagName) {
		this.tagIndex.add(new TagIndex(tagName, this.tagIndex.size() + 1));
	}

	public List<TagIndex> getTagIndex() {
		return this.tagIndex;
	}

	/**
	 * This is used to determine the max. amount of join-indices for the iteration of the join-index.
	 * If we have only one tag, we don't need a join index, if we got two then we need one, if we got
	 * three then we need two, and so on.<br/> We had to introduce this because iBATIS can only call
	 * methods that are true getter or setter. A call to tagIndex.size() is not possible. An attempt
	 * fails with "There is no READABLE property named 'size' in class 'java.util.ArrayList'".
	 */
	public int getMaxTagIndex() {
		return this.tagIndex.size();
	}
}