package org.bibsonomy.pingback;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author rja
 * @version $Id$
 */
public class ThreadedPingback extends SimplePingback implements Runnable {

	private final Queue<Post<? extends Resource>> queue = new ConcurrentLinkedQueue<Post<? extends Resource>>();
	private final boolean run = true;
	private final long waitTime = 1000;
	
	
	@Override
	public void sendPingback(Post<? extends Resource> post) {
		queue.add(post);
	}

	@Override
	public void run() {
		while (run) {
			if (queue.isEmpty()) {
				try {
					wait(waitTime);
				} catch (InterruptedException ex) {
					// noop
				}
			}
			super.sendPingback(queue.poll());
		}
		
	}

}
