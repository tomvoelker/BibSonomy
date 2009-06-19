package org.bibsonomy.util.filter.posts.matcher;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.springframework.beans.factory.annotation.Required;

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
	
	
	public Matcher getMatcher() {
		return matcher;
	}
	@Required
	public void setMatcher(Matcher matcher) {
		this.matcher = matcher;
	}

}

