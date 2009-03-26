package org.bibsonomy.recommender.tags.database.params;

/**
 * @author fei
 * @version $Id$
 */
public class ListParam {
	private Integer offset = 0;
	private Integer range = 0;
	
	public Integer getOffset() {
		return offset;
	}
	public Integer getRange() {
		return range;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public void setRange(Integer range) {
		this.range = range;
	}
}
