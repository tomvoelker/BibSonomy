package org.bibsonomy.pingback;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class TrackbackLinkTest {

	@Test
	public void testIsPingbackEnabled() {
		assertFalse(new TrackbackLink("Title", "http://www.example.com/", null, false).isPingbackEnabled());
		assertFalse(new TrackbackLink("Title", "http://www.example.com/", null, true).isPingbackEnabled());
		assertFalse(new TrackbackLink("Title", "http://www.example.com/", "http://www.example.com/pingback", true).isPingbackEnabled());
	}

	@Test
	public void testIsTrackbackEnabled() {
		assertFalse(new TrackbackLink("Title", "http://www.example.com/", null, false).isTrackbackEnabled());
		assertFalse(new TrackbackLink("Title", "http://www.example.com/", null, true).isTrackbackEnabled());
		assertFalse(new TrackbackLink("Title", "http://www.example.com/", "", false).isTrackbackEnabled());
		assertFalse(new TrackbackLink("Title", "http://www.example.com/", "", true).isTrackbackEnabled());
		assertTrue(new TrackbackLink("Title", "http://www.example.com/", "http://www.example.com/pingback", true).isTrackbackEnabled());
	}

}
