package org.bibsonomy.util.filter.posts.matcher;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.springframework.beans.factory.annotation.Required;


/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class BooleanAllAndMatcher implements AllMatcher {

	private List<Matcher> matchers;
	
	public BooleanAllAndMatcher() {
		matchers = new LinkedList<Matcher>();
	}
	
	public BooleanAllAndMatcher(Matcher[] matchers) {
		super();
		this.matchers = Arrays.asList(matchers);
	}

	@Override
	public boolean matches(Post<? extends Resource> post) {
		for (final Matcher matcher: matchers) {
			if (!matcher.matches(post)) return false;
		}
		return true;
	}
	
	public Matcher[] getMatchers() {
		return matchers.toArray(new Matcher[matchers.size()]);
	}

	@Required
	public void setMatchers(Matcher[] matchers) {
		this.matchers = Arrays.asList(matchers);
	}

	@Override
	public String toString() {
		return "&" + matchers;
	}

	@Override
	public void addMatcher(Matcher matcher) {
		this.matchers.add(matcher);
		
	}
}

