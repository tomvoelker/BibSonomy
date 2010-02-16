package org.bibsonomy.rest.strategy.posts.standard;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.users.PostPostStrategy;

/**
 * strategy for creating standard posts
 * 
 * @author dzo
 * @version $Id$
 */
public class PostStandardPostStrategy extends PostPostStrategy {

	/**
	 * sets the context
	 * @param context
	 * @param username 
	 */
	public PostStandardPostStrategy(Context context, final String username) {
		super(context, username);
	}
	
	@Override
	protected Post<? extends Resource> parsePost() {
		return this.getRenderer().parseStandardPost(this.doc);
	}
	
	@Override
	public void validate() throws ValidationException {
		// TODO gold standard access rules TODODZ
		throw new ValidationException("");
	}
}
