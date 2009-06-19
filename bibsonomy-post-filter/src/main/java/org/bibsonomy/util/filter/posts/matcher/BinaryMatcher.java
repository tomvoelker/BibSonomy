package org.bibsonomy.util.filter.posts.matcher;


/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public interface BinaryMatcher extends Matcher {

	public void setLeft(Matcher left);
	public void setRight(Matcher right);
	
}

