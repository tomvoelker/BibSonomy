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
	private final long waitTime = 1000;
	private boolean run = true;
	
	
	@Override
	public void sendPingback(Post<? extends Resource> post) {
		queue.add(post);
	}

	public void stop() {
		this.run = false;
	}
	
	@Override
	public void run() {
		while (this.run) {
			if (this.queue.isEmpty()) {
				try {
					wait(this.waitTime);
				} catch (InterruptedException ex) {
					// noop
				}
			}
			super.sendPingback(this.queue.poll());
		}
	}

}
