package org.bibsonomy.pingback;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author jensi
  */
public class MockPingback implements ThreadedPingBack {

	@Override
	public String sendPingback(Post<? extends Resource> post) {
		return null;
	}

	@Override
	public void clearQueue() {
		// noop
	}

}
