package org.bibsonomy.webapp.command;

/**
 * bean for a page in a multipaged listview context
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class PageCommand {
	private Integer number;
	private int start;
	
	/**
	 * default bean constructor
	 */
	public PageCommand() {
	}
	
	/**
	 * @param number index of this page (normally displayed in the view and
	 *        therefore starting with 1) 
	 * @param start index of the first entity in the sublist on this page
	 *        (starting with 0)
	 */
	public PageCommand(final Integer number, final int start) {
		this.number = number;
		this.start = start;
	}
	
	/**
	 * @return index of this page (normally displayed in the view and
	 *         therefore starting with 1). May be null if unknown
	 */
	public Integer getNumber() {
		return this.number;
	}
	
	/**
	 * @param number index of this page (normally displayed in the view and
	 *        therefore starting with 1). May be null if unknown
	 */
	public void setNumber(final Integer number) {
		this.number = number;
	}
	
	/**
	 * @return index of the first entity in the sublist on this page
	 *         (starting with 0)
	 */
	public int getStart() {
		return this.start;
	}
	
	/**
	 * @param start index of the first entity in the sublist on this page
	 *              (starting with 0)
	 */
	public void setStart(final int start) {
		this.start = start;
	}
}
