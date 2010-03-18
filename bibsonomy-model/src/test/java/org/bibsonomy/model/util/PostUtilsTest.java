/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class PostUtilsTest {

	@Test
	public void testSetGroupIdsPostOfQextendsResourceBoolean() {
		final Post<Bookmark> post = new Post<Bookmark>();
		final Set<Group> groups = new HashSet<Group>();
		
		final Group group1 = new Group(0);
		final Group group2 = new Group("kde");
		final Group group3 = new Group(7);
		
		groups.add(group1);
		groups.add(group2);
		groups.add(group3);
		post.setGroups(groups);
		
		/*
		 * set to non-spammer
		 */
		PostUtils.setGroupIds(post, false);
		Assert.assertEquals(0, group1.getGroupId());
		// FIXME: bug in UserUtils.getGroupId	
//		Assert.assertEquals(GroupID.INVALID.getId(), group2.getGroupId());
		Assert.assertEquals(7, group3.getGroupId());
		
		/*
		 * set to spammer
		 */
		PostUtils.setGroupIds(post, true);
		Assert.assertEquals(-2147483648, group1.getGroupId());
		// FIXME: bug in UserUtils.getGroupId
//		Assert.assertEquals(0, group2.getGroupId());
		Assert.assertEquals(-2147483641, group3.getGroupId());		

		/*
		 * set to spammer
		 */
		PostUtils.setGroupIds(post, true);
		Assert.assertEquals(-2147483648, group1.getGroupId());
		// FIXME: bug in UserUtils.getGroupId
//		Assert.assertEquals(0, group2.getGroupId());
		Assert.assertEquals(-2147483641, group3.getGroupId());		

		/*
		 * set to non-spammer
		 */
		PostUtils.setGroupIds(post, false);
		Assert.assertEquals(0, group1.getGroupId());
		// FIXME: bug in UserUtils.getGroupId	
//		Assert.assertEquals(GroupID.INVALID.getId(), group2.getGroupId());
		Assert.assertEquals(7, group3.getGroupId());
	}

	@Test
	public void testGetInstance() throws Exception {
		assertEquals(Bookmark.class, PostUtils.getInstance("bookmark").getResource().getClass());
		assertEquals(BibTex.class, PostUtils.getInstance("bibtex").getResource().getClass());
	}
}
