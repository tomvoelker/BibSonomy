package org.bibsonomy.util.filter.posts.matcher;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;


/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class BooleanAllOrMatcher implements Matcher {

	private Matcher[] matchers;
	
	public BooleanAllOrMatcher() {
		// TODO Auto-generated constructor stub
	}
	
	public BooleanAllOrMatcher(Matcher[] matchers) {
		super();
		this.matchers = matchers;
	}

	@Override
	public boolean matches(Post<? extends Resource> post) {
		for (final Matcher matcher: matchers) {
			if (matcher.matches(post)) return true;
		}
		return false;
	}

	public Matcher[] getMatchers() {
		return matchers;
	}

	public void setMatchers(Matcher[] matchers) {
		this.matchers = matchers;
	}


}

