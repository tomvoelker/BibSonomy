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

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import com.malethan.pingback.Link;
import com.malethan.pingback.PingbackClient;
import com.malethan.pingback.PingbackException;

/**
 * @author rja
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
			assertThat(e.getMessage(), startsWith("request error: " + UnknownHostException.class.getName() + ": www.kde.kde.cs.uni-kassel.de"));
			assertEquals(PingbackClient.UPSTREAM_PROBLEM, e.getFaultCode());
		}
	}

	
}
