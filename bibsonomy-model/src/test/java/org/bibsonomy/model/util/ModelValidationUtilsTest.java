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

package org.bibsonomy.model.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.junit.Test;

/**
 * TODO: more tests
 * 
 * @author dzo
 * @version $Id$
 */
public class ModelValidationUtilsTest {
	
	private static final String BOOKMARK_INVALID_URL_MESSAGE = "found a bookmark without url assigned.";
	private static final String BOOKMARK_INVALID_HASH_MESSAGE = "found a bookmark without hash assigned.";

	/**
	 * tests checkUser
	 */
	@Test
	public void testUser() {
		final User user = new User();
		
		try {
			ModelValidationUtils.checkUser(user);
			fail("expected InvalidModelException");
		} catch (final InvalidModelException e) {
		}
		
		user.setName("");
		
		try {
			ModelValidationUtils.checkUser(user);
			fail("expected InvalidModelException");
		} catch (final InvalidModelException e) {
		}
		
		user.setName("Test");
		ModelValidationUtils.checkUser(user);
	}
	
	/**
	 * tests a tag
	 */
	@Test
	public void testTag() {
		final Tag tag = new Tag();
		try {
			ModelValidationUtils.checkTag(tag);
			fail("expected InvalidModelException");
		} catch (final InvalidModelException e) {
		}
		
		tag.setName("");
		
		try {
			ModelValidationUtils.checkTag(tag);
			fail("expected InvalidModelException");
		} catch (final InvalidModelException e) {
		}
		
		tag.setName("testtag");
		ModelValidationUtils.checkTag(tag);
	}
	
	/**
	 * tests if a group with name = null is invalid
	 */
	@Test
	public void testGroup() {
		final Group group = new Group();
		try {
			ModelValidationUtils.checkGroup(group);
			fail("expected InvalidModelException");
		} catch (final InvalidModelException e) {
		}
		
		group.setName("");
		
		try {
			ModelValidationUtils.checkGroup(group);
			fail("expected InvalidModelException");
		} catch (final InvalidModelException e) {
		}
		
		group.setName("testgroup");
		ModelValidationUtils.checkGroup(group);
	}
	
	/**
	 * tests checkBookmark
	 */
	@Test
	public void testBookmark() {
		final Bookmark bookmark = new Bookmark();
		bookmark.recalculateHashes();
		try {
			ModelValidationUtils.checkBookmark(bookmark);
		} catch (InvalidModelException ex) {
			assertEquals(BOOKMARK_INVALID_URL_MESSAGE, ex.getMessage());
		}
		
		bookmark.setUrl("");
		try {
			ModelValidationUtils.checkBookmark(bookmark);
		} catch (InvalidModelException ex) {
			assertEquals(BOOKMARK_INVALID_URL_MESSAGE, ex.getMessage());
		}
		
		// set url => valid model
		bookmark.setUrl("http://localhost:8080");
		bookmark.recalculateHashes();
		ModelValidationUtils.checkBookmark(bookmark);
		
		bookmark.setInterHash(null);
		
		try {
			ModelValidationUtils.checkBookmark(bookmark);
		} catch (InvalidModelException ex) {
			assertEquals(BOOKMARK_INVALID_HASH_MESSAGE, ex.getMessage());
		}
		
		bookmark.setInterHash("");
		try {
			ModelValidationUtils.checkBookmark(bookmark);
		} catch (InvalidModelException ex) {
			assertEquals(BOOKMARK_INVALID_HASH_MESSAGE, ex.getMessage());
		}
		
		bookmark.setIntraHash(null);
		try {
			ModelValidationUtils.checkBookmark(bookmark);
		} catch (InvalidModelException ex) {
			assertEquals(BOOKMARK_INVALID_HASH_MESSAGE, ex.getMessage());
		}
		
		bookmark.setIntraHash("");
		try {
			ModelValidationUtils.checkBookmark(bookmark);
		} catch (InvalidModelException ex) {
			assertEquals(BOOKMARK_INVALID_HASH_MESSAGE, ex.getMessage());
		}
	}
	
	/**
	 * tests if a publication with title = null is invalid
	 */
	@Test
	public void testPublication() {
		final BibTex publication = new BibTex();
		try {
			ModelValidationUtils.checkPublication(publication);
		} catch (InvalidModelException ex) {
		}
		
		publication.setTitle("");
		try {
			ModelValidationUtils.checkPublication(publication);
		} catch (InvalidModelException ex) {
		}
		
		publication.setTitle("title");		
		ModelValidationUtils.checkPublication(publication);
	}
}
