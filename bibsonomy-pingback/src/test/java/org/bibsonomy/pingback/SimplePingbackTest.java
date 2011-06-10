package org.bibsonomy.pingback;

import java.util.Collections;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.services.URLGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.testing.ServletTester;

import com.malethan.pingback.impl.ApachePingbackClient;

/**
 * @author rja
 * @version $Id$
 */
public class SimplePingbackTest {
	
	private ServletTester tester;
	private String baseUrl;
	private SimplePingback pingback;
	
	@Before
	public void setUp() throws Exception {
		/*
		 * set up server
		 */
		tester = new ServletTester();
		tester.setContextPath("/");
		tester.addServlet(TestServlet.class, "/*");
		baseUrl = tester.createSocketConnector(true);
		tester.start();
		/*
		 * set up pingback
		 */
		this.pingback = new SimplePingback();
		this.pingback.setLinkLoader(new HttpClientLinkLoader());
		this.pingback.setPingbackClient(new ApachePingbackClient());
		this.pingback.setUrlGenerator(new URLGenerator(baseUrl + "/"));
	}
	
	@After
	public void shutDown() throws Exception {
		tester.stop();
	}
	
	
	@Test
	public void testSendPingback() {
		final Bookmark bookmark = new Bookmark();
		final Post<Bookmark> post = new Post<Bookmark>();
		post.setGroups(Collections.singleton(GroupUtils.getPublicGroup()));
		post.setResource(bookmark);
		post.setUser(new User("jaeschke"));
	
		bookmark.setUrl(baseUrl + "/pingback?body=true");
		this.pingback.sendPingback(post);
		
		
		bookmark.setUrl(baseUrl + "/pingback?body=true&header=true");
		this.pingback.sendPingback(post);
	}

}
