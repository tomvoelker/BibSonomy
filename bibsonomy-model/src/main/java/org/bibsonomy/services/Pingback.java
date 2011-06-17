package org.bibsonomy.services;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * 
 * 
 * @author rja
 * @version $Id$
 */
public interface Pingback {

	/**
	 * Sends a pingback for the provided post. Implementations must ensure that
	 * <ul>
	 * <li>pingbacks are send only for publicly visible posts,</li>
	 * <li>the method does not block, i.e., the pingback is sent asynchronously</li>
	 * </ul>
	 * 
	 * @param post
	 */
	public String sendPingback(final Post<? extends Resource> post);
}
