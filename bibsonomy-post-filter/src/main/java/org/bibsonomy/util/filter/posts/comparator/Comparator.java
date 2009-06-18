package org.bibsonomy.util.filter.posts.comparator;


/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public interface Comparator<T> {

	/**
	 * @param a - value in the post
	 * @param b - value to compare against
	 * @return
	 */
	public boolean compare(T a, T b);
	
}

