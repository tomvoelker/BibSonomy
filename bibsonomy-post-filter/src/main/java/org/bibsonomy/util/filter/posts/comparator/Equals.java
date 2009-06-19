package org.bibsonomy.util.filter.posts.comparator;


/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class Equals<T> implements Comparator<T> {

	@Override
	public boolean compare(T a, T b) {
		return a.equals(b);
	}

	@Override
	public String toString() {
		return "=";
	}
}

