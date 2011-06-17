package org.bibsonomy.pingback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.services.URLGenerator;
import org.junit.Before;
import org.junit.Test;

import com.malethan.pingback.impl.ApachePingbackClient;

/**
 * @author rja
 * @version $Id$
 */
public class SimplePingbackTest extends AbstractClientTest {
	
	private SimplePingback pingback;
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		/*
		 * set up pingback
		 */
		this.pingback = new SimplePingback();
		this.pingback.setLinkLoader(new HttpClientLinkLoader());
		this.pingback.setPingbackClient(new ApachePingbackClient());
		this.pingback.setTrackbackClient(new TrackbackClient());
		this.pingback.setUrlGenerator(new URLGenerator("http://www.bibsonomy.org/"));
	}
	
	
	@Test
	public void testSendPingback() {
		final Bookmark bookmark = new Bookmark();
		final Post<Bookmark> post = new Post<Bookmark>();
		post.setGroups(Collections.singleton(GroupUtils.getPublicGroup()));
		post.setResource(bookmark);
		post.setUser(new User("jaeschke"));
	
		bookmark.setUrl(baseUrl + "/pingback?body=true");
		assertEquals("pingback: success", this.pingback.sendPingback(post));
		
		
		bookmark.setUrl(baseUrl + "/pingback?body=true&header=true");
		assertEquals("pingback: success", this.pingback.sendPingback(post));
		
		/*
		 * no pingback registered for this URL
		 */
		bookmark.setUrl(baseUrl + "/foo");
		assertNull(this.pingback.sendPingback(post));
	}
	
	@Test
	public void testTrackPingback() {
		final Bookmark bookmark = new Bookmark();
		final Post<Bookmark> post = new Post<Bookmark>();
		post.setGroups(Collections.singleton(GroupUtils.getPublicGroup()));
		post.setResource(bookmark);
		post.setUser(new User("jaeschke"));
	
		bookmark.setUrl(baseUrl + "/article");
		assertEquals("trackback: success", this.pingback.sendPingback(post));

		/*
		 * no trackback registered for this URL
		 */
		bookmark.setUrl(baseUrl + "/foo");
		assertNull(this.pingback.sendPingback(post));
	}

}
