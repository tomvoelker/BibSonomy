package org.bibsonomy.model;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * A basket that holds some posts for a user.
 * 
 * TODO: implement full basket functionality
 *
 *
 * @version: $Id$
 * @author:  dbenz
 * $Author$
 *
 */
public class Basket {
	private static final Logger LOGGER = Logger.getLogger(Basket.class);
	
	private List<Post<BibTex>> posts;
	
	private int numPosts;

	public int getNumPosts() {
		return this.numPosts;
	}

	public void setNumPosts(int numPosts) {
		this.numPosts = numPosts;
	}
		
}
