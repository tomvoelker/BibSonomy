package org.bibsonomy.ibatis.params;

import org.bibsonomy.ibatis.enums.ContentType;
import org.bibsonomy.ibatis.enums.GroupType;

public class ByTagNames {

	private String[] tags;
	private ContentType contentType;
	private GroupType groupType;
	private int limit;
	private int offset;

	public ByTagNames() {

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

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	/**
	 * Returns a FROM-clause like:<br/>
	 * 
	 * <pre>
	 * , tas t2, ..., tas tn
	 * </pre>
	 * 
	 * <br/> if there is more than one tag present
	 */
	public String getFrom() {
		if (this.tags.length > 1) {
			final int len = this.tags.length - 1;
			final StringBuilder rVal = new StringBuilder();
			for (int i = 0; i < len; i++) {
				rVal.append(", tas t" + (i+2));
			}
			return rVal.substring(0);
		} else {
			return "";
		}
	}

	/**
	 * Returns a WHERE-clause like:<br/>
	 * 
	 * <pre>
	 * lower(t1.tag_name) = lower(&quot;tag_1&quot;) AND ... AND lower(tn.tag_name)=lower(&quot;tag_n&quot;)
	 * </pre>
	 */
	public String getWhere() {
		final StringBuilder rVal = new StringBuilder();
		int i = 1;
		for (final String tag : this.tags) {
			rVal.append("lower(t"+i+".tag_name) = lower(\""+tag+"\")" + ((this.tags.length > 1 && i < this.tags.length)?" AND ":""));
			i++;
		}
		return rVal.substring(0);
	}
}