/*
 * Created on 14.10.2007
 */
package org.bibsonomy.webapp.command;

public class Page {
	private int number;
	private int start;
	
	public Page() {
	}
	
	public Page(int number, int start) {
		this.number = number;
		this.start = start;
	}
	
	public int getNumber() {
		return this.number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getStart() {
		return this.start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	
	
}
