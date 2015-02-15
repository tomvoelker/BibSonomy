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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author rja
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
