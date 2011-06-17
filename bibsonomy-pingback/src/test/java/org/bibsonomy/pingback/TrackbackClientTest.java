package org.bibsonomy.pingback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.UnknownHostException;

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
		final Link link = new TrackbackLink("Title", baseUrl + "/article", baseUrl + "/article/trackback", true);
		assertEquals("success", trackbackClient.sendPingback("http://www.bibsonomy.org/article", link));
		
		try {
			final Link link2 = new TrackbackLink("Title", baseUrl + "/article", baseUrl + "/trackback", true);
			trackbackClient.sendPingback("http://www.bibsonomy.org/article", link2);
			fail("expected " + PingbackException.class.getSimpleName());
		} catch (final PingbackException e) {
			assertEquals("unknown URL", e.getMessage());
			assertEquals(PingbackClient.UNKOWN_ERROR, e.getFaultCode());
		}
		
	}

	@Test
	public void testSendPingbackFailOnWrongUrl1() {
		final Link link = new TrackbackLink("Title", baseUrl + "/article", baseUrl + "/article/noTrackback", true);
		
		try {
			trackbackClient.sendPingback("http://www.bibsonomy.org/article", link);
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
		final Link link = new TrackbackLink("Title", "http://www.kde.cs.uni-kassel.de/article", "http://www.kde.cs.uni-kassel.de/", true);
		
		try {
			trackbackClient.sendPingback("http://www.bibsonomy.org/article", link);
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
		final Link link = new TrackbackLink("Title", "http://www.kde.kde.cs.uni-kassel.de/article", "http://www.kde.kde.cs.uni-kassel.de/trackback", true);
		
		try {
			trackbackClient.sendPingback("http://www.bibsonomy.org/article", link);
			fail("expected " + PingbackException.class.getSimpleName());
		} catch (final PingbackException e) {
			assertEquals("request error: " + UnknownHostException.class.getName() + ": www.kde.kde.cs.uni-kassel.de", e.getMessage());
			assertEquals(PingbackClient.UPSTREAM_PROBLEM, e.getFaultCode());
		}
	}

	
}
