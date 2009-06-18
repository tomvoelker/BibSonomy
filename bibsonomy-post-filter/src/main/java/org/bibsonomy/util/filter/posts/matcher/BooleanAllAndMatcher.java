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
public class BooleanAllAndMatcher implements Matcher {

	private Matcher[] matchers;
	
	public BooleanAllAndMatcher() {
		// TODO Auto-generated constructor stub
	}
	
	public BooleanAllAndMatcher(Matcher[] matchers) {
		super();
		this.matchers = matchers;
	}

	@Override
	public boolean matches(Post<? extends Resource> post) {
		for (final Matcher matcher: matchers) {
			if (!matcher.matches(post)) return false;
		}
		return true;
	}

	public Matcher[] getMatchers() {
		return matchers;
	}

	public void setMatchers(Matcher[] matchers) {
		this.matchers = matchers;
	}


}

