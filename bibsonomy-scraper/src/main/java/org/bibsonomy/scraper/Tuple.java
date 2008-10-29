package org.bibsonomy.scraper;

/**
 * A Tuple stores two objects of type P and Q. 
 * 
 * @author rja
 * @version $Id$
 * @param <P> type of first object.
 * @param <Q> type of second object.
 */
public class Tuple<P,Q> {
	
	private P first;
	private Q second;
	
	/**
	 * @param first
	 * @param second
	 */
	public Tuple(P first, Q second) {
		super();
		this.first = first;
		this.second = second;
	}
	/**
	 * @return The first object.
	 */
	public P getFirst() {
		return this.first;
	}
	/**
	 * @param first
	 */
	public void setFirst(P first) {
		this.first = first;
	}
	/**
	 * @return The second object.
	 */
	public Q getSecond() {
		return this.second;
	}
	/**
	 * @param second
	 */
	public void setSecond(Q second) {
		this.second = second;
	}
	
	
	

}
