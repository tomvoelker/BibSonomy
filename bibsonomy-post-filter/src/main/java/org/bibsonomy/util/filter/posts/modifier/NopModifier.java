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
public class NopModifier implements Modifier {


	@Override
	public boolean updatePost(Post<? extends Resource> post) {
		// TODO Auto-generated method stub
		return false;
	}
}

