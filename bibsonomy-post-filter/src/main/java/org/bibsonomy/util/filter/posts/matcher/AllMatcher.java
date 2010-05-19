package org.bibsonomy.util.filter.posts.matcher;


/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public interface AllMatcher extends Matcher {
	
	/**
	 * TODO: improve documentation
	 * @param matchers
	 */
	public void setMatchers(Matcher[] matchers);
	
	/**
	 * TODO: improve documentation
	 * @param matcher
	 */
	public void addMatcher(Matcher matcher);
}

