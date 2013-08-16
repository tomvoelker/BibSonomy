package org.bibsonomy.pingback;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.services.Pingback;

/**
 * @author jensi
 * @version $Id$
 */
public class MockPingback implements ThreadedPingBack {

	@Override
	public String sendPingback(Post<? extends Resource> post) {
		return null;
	}

	@Override
	public void clearQueue() {
	}

}
