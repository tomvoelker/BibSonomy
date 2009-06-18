package org.bibsonomy.util.filter.posts.comparator;


/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class LessOrEqual<T extends Comparable<T>> implements Comparator<T> {

	@Override
	public boolean compare(T a, T b) {
		return a.compareTo(b) < 0;
	}

	
}

