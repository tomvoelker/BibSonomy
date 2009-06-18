package org.bibsonomy.util.filter.posts.matcher;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Boolean AND.
 * 
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class NotMatcher implements Matcher {

	private Matcher matcher;

	public NotMatcher() {
		// nop
	}

	public NotMatcher(Matcher matcher) {
		super();
		this.matcher = matcher;
	}

	@Override
	public boolean matches(final Post<? extends Resource> post) {
		return !matcher.matches(post);
	}
	
	
	public Matcher getLeft() {
		return matcher;
	}
	public void setLeft(Matcher left) {
		this.matcher = left;
	}

}

