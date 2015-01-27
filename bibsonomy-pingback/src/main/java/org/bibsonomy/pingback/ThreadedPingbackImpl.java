/**
 * BibSonomy Pingback - Pingback/Trackback for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
 */
public class ThreadedPingbackImpl extends SimplePingback implements Runnable, ThreadedPingBack {
	private static final Log log = LogFactory.getLog(ThreadedPingbackImpl.class);
	
	private final Queue<Post<? extends Resource>> queue = new ConcurrentLinkedQueue<Post<? extends Resource>>();
	private final long waitTime = 1000;

	@Override
	public String sendPingback(Post<? extends Resource> post) {
		queue.add(post);
		return null;
	}

	// XXX: this is only used by tests
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
	@Override
	public void clearQueue() throws InterruptedException {
		log.debug("clearing queue (size = " + queue.size() + ")");
		while (!this.queue.isEmpty()) {
			log.debug(super.sendPingback(this.queue.poll()));
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
