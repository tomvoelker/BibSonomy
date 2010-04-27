/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class URLGeneratorTest {

	private static URLGenerator ug = new URLGenerator("http://www.bibsonomy.org/");
	
	@Test
	public void testGetPostUrl() {
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		assertEquals("http://www.bibsonomy.org/bibtex/" + HashID.INTRA_HASH.getId() + post.getResource().getIntraHash() + "/" + post.getUser().getName(), ug.getPublicationUrl(post.getResource(), post.getUser()));
		final Post<Bookmark> bPost = ModelUtils.generatePost(Bookmark.class);
		assertEquals("http://www.bibsonomy.org/url/" + bPost.getResource().getIntraHash() + "/" + bPost.getUser().getName(), ug.getPostUrl(bPost));

	}

	@Test
	public void testGetPublicationUrl() {
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		assertEquals("http://www.bibsonomy.org/bibtex/" + HashID.INTRA_HASH.getId() + post.getResource().getIntraHash() + "/" + post.getUser().getName(), ug.getPublicationUrl(post.getResource(), post.getUser()));
	}

	@Test
	public void testGetUserUrl() {
		assertEquals("http://www.bibsonomy.org/user/jaeschke", ug.getUserUrl(new User("jaeschke")));
	}
	
	@Test
	public void testMatch() throws Exception {
		assertTrue(ug.matchesPage("http://www.bibsonomy.org/inbox", URLGenerator.Page.INBOX));
		assertTrue(ug.matchesPage("http://www.bibsonomy.org/basket", URLGenerator.Page.BASKET));
		assertTrue(ug.matchesPage("http://www.bibsonomy.org/basket?start=0", URLGenerator.Page.BASKET));
		
		assertFalse(ug.matchesPage("http://www.bibsonomy.org/basket", URLGenerator.Page.INBOX));
		assertFalse(ug.matchesPage("http://www.bibsonomy.org/foo/basket", URLGenerator.Page.BASKET));
		assertFalse(ug.matchesPage("/basket", URLGenerator.Page.BASKET));
	}

}
