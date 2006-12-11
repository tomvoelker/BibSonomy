package org.bibsonomy.ibatis.params.generic;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.beans.TagIndex;
import org.bibsonomy.ibatis.params.generic.common.LimitOffset;

/**
 * Can be used to search tags by its name.<br/><br/>
 * By default its groupType is <em>public</em> and it isn't case sensitive.
 * 
 * @author Christian Schenk
 */
public abstract class ByTagNames extends LimitOffset {

	/** List of (tagname, index)-pairs */
	private final List<TagIndex> tagIndex;
	protected ConstantID contentType;
	/** By default it's public */
	private ConstantID groupType;
	/** By default it's not case sensitive */
	private boolean caseSensitive;

	public ByTagNames() {
		this.tagIndex = new ArrayList<TagIndex>();
		this.groupType = ConstantID.GROUP_PUBLIC;
		this.caseSensitive = false;
	}

	public abstract int getContentType();

	public int getGroupType() {
		return this.groupType.getId();
	}

	public void setGroupType(ConstantID groupType) {
		this.groupType = groupType;
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