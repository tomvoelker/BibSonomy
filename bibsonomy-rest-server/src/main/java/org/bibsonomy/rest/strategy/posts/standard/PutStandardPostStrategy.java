package org.bibsonomy.rest.strategy.posts.standard;

import java.util.Date;

import org.bibsonomy.common.exceptions.ValidationException;
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
	public PutStandardPostStrategy(Context context, final String username, final String resourceHash) {
		super(context, username, resourceHash);
	}
	
	@Override
	protected Post<? extends Resource> getPost() {
		final Post<? extends Resource> post = this.getRenderer().parseStandardPost(this.doc);
		/*
		 * set postingdate to current time
		 */
		post.setDate(new Date(System.currentTimeMillis()));				
		/*
		 * set the (old) intrahash of the resource as specified in the URL
		 */
		post.getResource().setInterHash(this.resourceHash);
		return post;
	}
	
	@Override
	public void validate() throws ValidationException {
		// TODO: gold standard access rules TODODZ
		throw new ValidationException("");
	}
}
