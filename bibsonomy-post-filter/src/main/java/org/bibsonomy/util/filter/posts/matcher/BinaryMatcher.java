package org.bibsonomy.util.filter.posts.matcher;


/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public interface BinaryMatcher extends Matcher {

	/**
	 * TODO: improve documentation
	 * @param left
	 */
	public void setLeft(Matcher left);
	
	/**
	 * TODO: improve documentation
	 * @param right
	 */
	public void setRight(Matcher right);
	
}

