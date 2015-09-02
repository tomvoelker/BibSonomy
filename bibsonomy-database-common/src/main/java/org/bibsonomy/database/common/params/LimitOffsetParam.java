package org.bibsonomy.database.common.params;

/**
 * TODO: add documentation to this class
 * 
 * @author jensi
 */
public class LimitOffsetParam<T> {
	private T param;
	private int offset;
	private int limit;

	public T getParam() {
		return this.param;
	}

	public void setParam(T param) {
		this.param = param;
	}

	public int getOffset() {
		return this.offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return this.limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public int getEnd() {
		return this.offset + this.limit;
	}
}
