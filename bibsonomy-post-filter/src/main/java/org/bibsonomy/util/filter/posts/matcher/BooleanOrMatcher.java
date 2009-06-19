package org.bibsonomy.util.filter.posts.matcher;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.springframework.beans.factory.annotation.Required;

/**
 * Boolean OR.
 * 
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class BooleanOrMatcher implements Matcher {

	private Matcher left;
	private Matcher right;

	public BooleanOrMatcher() {
		// noop
	}
	
	public BooleanOrMatcher(Matcher left, Matcher right) {
		super();
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean matches(final Post<? extends Resource> post) {
		return left.matches(post) || right.matches(post);
	}

	public Matcher getLeft() {
		return left;
	}
	@Required
	public void setLeft(Matcher left) {
		this.left = left;
	}

	public Matcher getRight() {
		return right;
	}
	@Required
	public void setRight(Matcher right) {
		this.right = right;
	}
}

