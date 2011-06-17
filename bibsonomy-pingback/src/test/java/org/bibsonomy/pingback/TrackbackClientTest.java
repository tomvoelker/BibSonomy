package org.bibsonomy.pingback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.malethan.pingback.Link;
import com.malethan.pingback.PingbackClient;
import com.malethan.pingback.PingbackException;

/**
 * @author rja
 * @version $Id$
 */
public class TrackbackClientTest extends AbstractClientTest {

	private TrackbackClient trackbackClient;
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		this.trackbackClient = new TrackbackClient();
	}
	
	@Test
	public void testSendPingback() {
		final Link link = new TrackbackLink("Title", "http://www.example.com/", baseUrl + "/trackback", true);
		assertEquals("success", trackbackClient.sendPingback(baseUrl + "/article", link));
		
		try {
			trackbackClient.sendPingback(baseUrl + "/error", link);
			fail("expected " + PingbackException.class.getSimpleName());
		} catch (final PingbackException e) {
			assertEquals("unknown URL", e.getMessage());
			assertEquals(PingbackClient.UNKOWN_ERROR, e.getFaultCode());
		}
		
	}

	@Test
	public void testSendPingbackFailOnWrongUrl1() {
		final Link link = new TrackbackLink("Title", "http://www.example.com/", baseUrl + "/noTrackback", true);
		
		try {
			trackbackClient.sendPingback(baseUrl + "/article", link);
			fail("expected " + PingbackException.class.getSimpleName());
		} catch (final PingbackException e) {
			assertEquals("unknown error", e.getMessage());
			assertEquals(PingbackClient.UNKOWN_ERROR, e.getFaultCode());
		}
	}

	/**
	 * URL exists but has no trackback 
	 */
	@Test
	public void testSendPingbackFailOnWrongUrl2() {
		final Link link = new TrackbackLink("Title", "http://www.kde.cs.uni-kassel.de//", "http://www.example.com/noTrackback", true);
		
		try {
			trackbackClient.sendPingback(baseUrl + "/article", link);
			fail("expected " + PingbackException.class.getSimpleName());
		} catch (final PingbackException e) {
			assertEquals("unknown error", e.getMessage());
			assertEquals(PingbackClient.UNKOWN_ERROR, e.getFaultCode());
		}
	}
	
	/**
	 * URL does not exist
	 */
	@Test
	public void testSendPingbackFailOnWrongUrl3() {
		final Link link = new TrackbackLink("Title", "http://www.kde.kde.cs.uni-kassel.de/", "http://www.example.com/noTrackback", true);
		
		try {
			trackbackClient.sendPingback(baseUrl + "/article", link);
			fail("expected " + PingbackException.class.getSimpleName());
		} catch (final PingbackException e) {
			assertEquals("unknown error", e.getMessage());
			assertEquals(PingbackClient.UNKOWN_ERROR, e.getFaultCode());
		}
	}

	
}
