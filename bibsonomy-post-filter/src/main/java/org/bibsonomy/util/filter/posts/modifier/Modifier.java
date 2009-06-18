package org.bibsonomy.util.filter.posts.modifier;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public interface Modifier {

	public boolean updatePost(final Post<? extends Resource> post);
	
}

