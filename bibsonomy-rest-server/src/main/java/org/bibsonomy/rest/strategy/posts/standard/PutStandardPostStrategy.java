package org.bibsonomy.rest.strategy.posts.standard;

import java.util.Date;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.users.PutPostStrategy;

/**
 * @author dzo
 * @version $Id$
 */
public class PutStandardPostStrategy extends PutPostStrategy {

	/**
	 * sets the context
	 * @param context
	 * @param username 
	 * @param resourceHash 
	 */
	public PutStandardPostStrategy(final Context context, final String username, final String resourceHash) {
		super(context, username, resourceHash);
	}
	
	@Override
	protected Post<? extends Resource> getPost() {
		final Post<? extends Resource> post = this.getRenderer().parseStandardPost(this.doc);
		/*
		 * set postingdate to current time
		 */
		post.setDate(new Date());				
		/*
		 * set the (old) intrahash of the resource as specified in the URL
		 */
		post.getResource().setIntraHash(this.resourceHash);
		return post;
	}
}
