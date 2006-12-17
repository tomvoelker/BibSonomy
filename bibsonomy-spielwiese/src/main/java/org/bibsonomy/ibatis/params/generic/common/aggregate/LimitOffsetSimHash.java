package org.bibsonomy.ibatis.params.generic.common.aggregate;

import org.bibsonomy.ibatis.params.generic.common.SimHash;

/**
 * Had to copy this from LimitOffset, because I don't know a proper workaround
 * for multiple inheritance in Java. FIXME copied code
 * 
 * @author Christian Schenk
 */
public class LimitOffsetSimHash extends SimHash {

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