package org.bibsonomy.ibatis.params;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.ibatis.enums.ContentType;
import org.bibsonomy.ibatis.enums.GroupType;

public class ByTagNames {

	private final List<TagIndex> tagIndex;
	private ContentType contentType;
	private GroupType groupType;
	private int limit;
	private int offset;
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
}