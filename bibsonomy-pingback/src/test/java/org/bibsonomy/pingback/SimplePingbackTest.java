/**
 * BibSonomy Pingback - Pingback/Trackback for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
		post.setGroups(Collections.singleton(GroupUtils.buildPublicGroup()));
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
		post.setGroups(Collections.singleton(GroupUtils.buildPublicGroup()));
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
