package org.bibsonomy.model;

import java.util.List;

/**
 * A basket that holds some posts for a user.
 * 
 * TODO: implement full basket functionality
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class Basket {

	private List<Post<BibTex>> posts;

	private int numPosts;

	/**
	 * @return numPosts
	 */
	public int getNumPosts() {
		return this.numPosts;
	}

	/**
	 * @param numPosts
	 */
	public void setNumPosts(int numPosts) {
		this.numPosts = numPosts;
	}
}