/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class PostTest {

	/**
	 * tests addTag
	 */
	@Test
	public void addTag() {
		Post<BibTex> post = new Post<BibTex>();
		assertEquals(0, post.getTags().size());
		post.addTag("tag1");
		post.addTag("tag2");
		assertEquals(2, post.getTags().size());

		// don't call getTags before addTag
		post = new Post<BibTex>();
		post.addTag("tag1");
		post.addTag("tag2");
		assertEquals(2, post.getTags().size());
	}

	/**
	 * tests addGroup
	 */
	@Test
	public void addGroup() {
		Post<BibTex> post = new Post<BibTex>();
		assertEquals(0, post.getGroups().size());
		post.addGroup("testgroup1");
		post.addGroup("testgroup2");
		assertEquals(2, post.getGroups().size());

		// don't call getGroups before addGroup
		post = new Post<BibTex>();
		post.addGroup("testgroup1");
		post.addGroup("testgroup2");
		assertEquals(2, post.getGroups().size());
	}
}