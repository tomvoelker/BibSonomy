/**
 * BibSonomy Pingback - Pingback/Trackback for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.services.URLGenerator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.malethan.pingback.impl.ApachePingbackClient;

/**
 * @author rja
 */
public class ThreadedPingbackTest extends AbstractClientTest {

	private ThreadedPingbackImpl pingback;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		/*
		 * set up pingback
		 */
		this.pingback = new ThreadedPingbackImpl();
		this.pingback.setLinkLoader(new HttpClientLinkLoader());
		this.pingback.setPingbackClient(new ApachePingbackClient());
		this.pingback.setUrlGenerator(new URLGenerator(baseUrl + "/"));
	}

	@Test
	public void testSendPingback() {
		//		fail("Not yet implemented");
	}

	/**
	 * Tests starting and stopping the thread 
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testRun() throws InterruptedException {
		final Thread thread = new Thread(this.pingback);
		thread.start();
		Thread.sleep(1000);
		thread.interrupt();
	}

	@Test
	public void testBeanScheduling() throws InterruptedException {
		final ApplicationContext ctx = new ClassPathXmlApplicationContext("testBeanConfig.xml", ThreadedPingbackTest.class);
		final ThreadedPingbackImpl myPingback = ctx.getBean("pingback", ThreadedPingbackImpl.class);

		
		myPingback.sendPingback(getPost("http://www.biblicious.org/0"));
		myPingback.sendPingback(getPost("http://www.biblicious.org/1"));
		myPingback.sendPingback(getPost("http://www.biblicious.org/2"));
		myPingback.sendPingback(getPost("http://www.biblicious.org/3"));
		myPingback.sendPingback(getPost("http://www.biblicious.org/4"));
		myPingback.sendPingback(getPost("http://www.biblicious.org/5"));
		
		myPingback.sendPingback(getPost(baseUrl + "/pingback?body=true"));
//		myPingback.sendPingback(getPost(baseUrl + "/pingback?body=true&header=true"));
		
		Collection<Post<? extends Resource>> queue = myPingback.getQueue();

		while (!queue.isEmpty()) {
//			System.out.println("|queue| = " + queue.size());
			Thread.sleep(100);
			queue = myPingback.getQueue();
		}
		/*
		 * wait a bit until the server has finished serving requests
		 */
		Thread.sleep(100);
		assertTrue(queue.isEmpty());
	}

	private Post<Bookmark> getPost(final String url) {
		final Bookmark bookmark = new Bookmark();
		final Post<Bookmark> post = new Post<Bookmark>();
		post.setGroups(Collections.singleton(GroupUtils.buildPublicGroup()));
		post.setResource(bookmark);
		post.setUser(new User("jaeschke"));
		bookmark.setUrl(url);
		return post;
	}

}
