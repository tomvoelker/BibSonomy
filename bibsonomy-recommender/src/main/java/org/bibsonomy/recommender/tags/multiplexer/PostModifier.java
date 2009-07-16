package org.bibsonomy.recommender.tags.multiplexer;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Post modifiers arbitrarily change a post's content.
 * 
 * @author fei
 * @version $Id$
 */
public interface PostModifier {

	/**
	 * post modifiers arbitrarily change a post's content
	 * 
	 * @param post the post to filter
	 */
	public void alterPost(Post<? extends Resource> post);

}