package org.bibsonomy.util.filter.posts.comparator;


/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class Matches implements Comparator<String> {

	@Override
	public boolean compare(String a, String b) {
		return a.matches(b);
	}

	
}

