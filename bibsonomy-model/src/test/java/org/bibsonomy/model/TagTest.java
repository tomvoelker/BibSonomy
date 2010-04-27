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

package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class TagTest {

	/**
	 * tests the basics
	 */
	@Test
	public void simple() {
		// constructor
		assertNull(new Tag().getName());
		assertEquals("test-tag", new Tag("test-tag").getName());

		// lazy initialization
		assertNotNull(new Tag().getPosts());
		assertNotNull(new Tag().getSubTags());
		assertNotNull(new Tag().getSuperTags());
	}

	/**
	 * Checks that the copy constructor indeed returns a deep copy
	 * of the tag.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCopyConstructor() throws Exception {
		final Tag iccs = new Tag("iccs");
		final Tag conference = new Tag("conference");
		conference.addSubTag(iccs);
		conference.setGlobalcount(10);
		final Tag event = new Tag("event");
		conference.addSuperTag(event);
		
		final Tag conferenceCopy = new Tag(conference);
		
		final Tag iccsCopy = conferenceCopy.getSubTags().get(0);
		final Tag eventCopy = conferenceCopy.getSuperTags().get(0);
		/*
		 * assert equal content
		 */
		assertEquals("iccs", iccsCopy.getName());
		assertEquals("event", eventCopy.getName());
		assertEquals(conference.getGlobalcount(), conferenceCopy.getGlobalcount());

		/*
		 * assert different instances
		 */
		assertNotSame(iccs, iccsCopy);
		assertNotSame(event, eventCopy);
		assertNotSame(conference, conferenceCopy);
	}

}