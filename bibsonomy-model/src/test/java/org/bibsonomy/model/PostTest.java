/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

/**
 * @author Christian Schenk
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
	
	/**
	 * Tests, if removing a tag from the tag set works.
	 */
	@Test 
	public void removeTag() {
		final Post<Bookmark> post = new Post<Bookmark>();
		final Set<Tag> tags = post.getTags();
		assertEquals(0, tags.size());
		
		tags.add(new Tag("tag"));
		tags.add(new Tag("sys:relevantFor:l3s"));
		
		assertEquals(2, tags.size());
		
		final Iterator<Tag> iterator = tags.iterator();
		while (iterator.hasNext()) {
			final Tag next = iterator.next();
			if (next.getName().equals("sys:relevantFor:l3s")) {
				iterator.remove();
			}
		}
		
		assertEquals(1, tags.size());
	}
}