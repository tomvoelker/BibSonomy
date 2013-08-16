package org.bibsonomy.pingback;

import org.bibsonomy.services.Pingback;

/**
 * @author jensi
 * @version $Id$
 */
public interface ThreadedPingBack extends Pingback {

	/**
	 * Clears the queue by sending pingbacks to all URLs in the queue.
	 * @throws InterruptedException 
	 */
	public void clearQueue() throws InterruptedException;

}