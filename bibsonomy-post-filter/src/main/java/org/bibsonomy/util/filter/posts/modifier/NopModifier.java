package org.bibsonomy.util.filter.posts.modifier;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * 
 * Does nothing.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class NopModifier implements Modifier {

	@Override
	public boolean updatePost(final Post<? extends Resource> post) {
		return false;
	}
}

