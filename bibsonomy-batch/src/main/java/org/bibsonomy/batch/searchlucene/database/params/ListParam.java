package org.bibsonomy.batch.searchlucene.database.params;

public class ListParam {
	private Integer offset;
	private Integer size;
	
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public Integer getSize() {
		return size;
	}
}
