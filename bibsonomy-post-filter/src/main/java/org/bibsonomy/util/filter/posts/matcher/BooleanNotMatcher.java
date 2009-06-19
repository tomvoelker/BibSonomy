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
public class BooleanNotMatcher implements UnaryMatcher {

	private Matcher matcher;

	public BooleanNotMatcher() {
		// nop
	}

	public BooleanNotMatcher(Matcher matcher) {
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
	
	@Override
	public String toString() {
		return " ! " + matcher;
	}

}

