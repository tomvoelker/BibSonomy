package org.bibsonomy.ibatis.params.generic.common;

/**
 * For limit/offset navigation.
 *
 * @author Christian Schenk
 */
public class LimitOffset {

	private int limit;
	private int offset;

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
}