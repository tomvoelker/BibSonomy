package org.bibsonomy.pingback;

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
 * @version $Id$
 */
public class ThreadedPingbackTest extends AbstractClientTest {

	private ThreadedPingback pingback;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		/*
		 * set up pingback
		 */
		this.pingback = new ThreadedPingback();
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
		final ThreadedPingback myPingback = ctx.getBean("pingback", ThreadedPingback.class);

		
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
			System.out.println("|queue| = " + queue.size());
			Thread.sleep(100);
			queue = myPingback.getQueue();
		}
		/*
		 * wait a bit until the server has finished serving requests
		 */
		Thread.sleep(100);

	}

	private Post<Bookmark> getPost(final String url) {
		final Bookmark bookmark = new Bookmark();
		final Post<Bookmark> post = new Post<Bookmark>();
		post.setGroups(Collections.singleton(GroupUtils.getPublicGroup()));
		post.setResource(bookmark);
		post.setUser(new User("jaeschke"));
		bookmark.setUrl(url);
		return post;
	}

}
