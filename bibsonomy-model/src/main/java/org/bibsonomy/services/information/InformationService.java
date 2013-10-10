package org.bibsonomy.services.information;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author dzo
 * @version $Id$
 */
public interface InformationService {
	
	/**
	 * @param username the user to inform
	 * @param post
	 */
	public void createdPost(final String username, final Post<? extends Resource> post);
}
