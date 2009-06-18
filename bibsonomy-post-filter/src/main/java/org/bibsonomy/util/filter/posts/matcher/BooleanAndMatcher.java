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
public class BooleanAndMatcher implements Matcher {

	private Matcher left;
	private Matcher right;


	public BooleanAndMatcher() {
		// nop
	}


	public BooleanAndMatcher(Matcher left, Matcher right) {
		super();
		this.left = left;
		this.right = right;
	}



	@Override
	public boolean matches(final Post<? extends Resource> post) {
		return left.matches(post) && right.matches(post);
	}
	
	
	public Matcher getLeft() {
		return left;
	}



	public void setLeft(Matcher left) {
		this.left = left;
	}



	public Matcher getRight() {
		return right;
	}



	public void setRight(Matcher right) {
		this.right = right;
	}

}

