package org.bibsonomy.model.logic.query;

/**
 * a basic query containing all basic
 *
 * @author dzo
 */
public class BasicQuery {

	private int start = 0;

	private int end = 10;

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}
}
