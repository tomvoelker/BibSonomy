package org.bibsonomy.lucene.database.params;

/**
 * @author fei
 * @version $Id$
 */
public class ListParam {
	private Integer offset;
	private Integer size;
	
	/**
	 * @return the offset
	 */
	public Integer getOffset() {
		return offset;
	}
	
	/**
	 * @param offset the offset to set
	 */
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	
	/**
	 * @return the size
	 */
	public Integer getSize() {
		return size;
	}
	
	/**
	 * @param size the size to set
	 */
	public void setSize(Integer size) {
		this.size = size;
	}
}
