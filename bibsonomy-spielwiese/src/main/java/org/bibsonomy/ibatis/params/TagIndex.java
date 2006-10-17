package org.bibsonomy.ibatis.params;

public class TagIndex {
	private final String tagName;
	private final int index;

	public TagIndex(final String tagName, final int index) {
		this.tagName = tagName;
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}

	public String getTagName() {
		return this.tagName;
	}
}