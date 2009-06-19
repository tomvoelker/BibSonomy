package org.bibsonomy.util.filter.posts.matcher;


/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public interface AllMatcher extends Matcher {

	public void setMatchers(Matcher[] matchers);
	
	public void addMatcher(Matcher matcher);
}

