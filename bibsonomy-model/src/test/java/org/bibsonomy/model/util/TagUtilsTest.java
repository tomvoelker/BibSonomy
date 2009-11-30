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

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.bibsonomy.model.Tag;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class TagUtilsTest {

	@Test
	public void testGetMaxUserCount() {
//		fail("Not yet implemented");
	}

	@Test
	public void testGetMaxGlobalcountCount() {
//		fail("Not yet implemented");
	}

	@Test
	public void testToTagString() {
		
		final List<Tag> tags = new LinkedList<Tag>();
		
		Assert.assertEquals("", TagUtils.toTagString(tags, " "));

		tags.add(new Tag("foo"));
		
		Assert.assertEquals("foo", TagUtils.toTagString(tags, " "));

		tags.add(new Tag("bar"));
		
		Assert.assertEquals("foo bar", TagUtils.toTagString(tags, " "));

		tags.add(new Tag("blubb"));
		
		Assert.assertEquals("foo bar blubb", TagUtils.toTagString(tags, " "));

	}

}
