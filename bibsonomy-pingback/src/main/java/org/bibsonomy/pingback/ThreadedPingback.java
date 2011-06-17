package org.bibsonomy.pingback;

import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Threaded version of {@link SimplePingback}. 
 * 
 * @author rja
 * @version $Id$
 */
public class ThreadedPingback extends SimplePingback implements Runnable {
	private static final Log log = LogFactory.getLog(ThreadedPingback.class);
	
	private final Queue<Post<? extends Resource>> queue = new ConcurrentLinkedQueue<Post<? extends Resource>>();
	private final long waitTime = 1000;

	@Override
	public String sendPingback(Post<? extends Resource> post) {
		queue.add(post);
		return null;
	}


	@Override
	public void run() {
		while (true) {
			try {
				clearQueue();
				Thread.sleep(this.waitTime);
			} catch (InterruptedException ex) {
				log.warn("pingback interupted, still " + queue.size() + " URLs in queue");
				return; // stop execution
			}
		}
	}


	/**
	 * Clears the queue by sending pingbacks to all URLs in the queue.
	 * @throws InterruptedException 
	 */
	public void clearQueue() throws InterruptedException {
		log.debug("clearing queue (size = " + queue.size() + ")");
		while (!this.queue.isEmpty()) {
			super.sendPingback(this.queue.poll());
			Thread.sleep(100);// wait a bit between pings
		}
		log.debug("finished");
	}
	
	/**
	 * @return An unmodifiable version of the queue holding the URLs to be pinged.
	 */
	protected Collection<Post<? extends Resource>> getQueue() {
		return Collections.unmodifiableCollection(queue);
	}

}
