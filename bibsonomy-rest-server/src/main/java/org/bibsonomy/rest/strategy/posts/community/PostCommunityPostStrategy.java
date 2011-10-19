package org.bibsonomy.rest.strategy.posts.community;

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
public class PostCommunityPostStrategy extends PostPostStrategy {

	/**
	 * sets the context
	 * @param context
	 * @param username 
	 */
	public PostCommunityPostStrategy(final Context context, final String username) {
		super(context, username);
	}
	
	@Override
	protected Post<? extends Resource> parsePost() {
		return this.getRenderer().parseCommunityPost(this.doc);
	}
}
