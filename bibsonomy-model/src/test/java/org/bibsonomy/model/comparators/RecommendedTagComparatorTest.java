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

package org.bibsonomy.model.comparators;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author rja
 * @version $Id$
 */
public class RecommendedTagComparatorTest {

	private final static RecommendedTag[] tags = new RecommendedTag[] {
		new RecommendedTag("d", 1.0, 0.0),
		new RecommendedTag("c", 0.5, 0.0),
		new RecommendedTag("b", 0.25, 0.0),
		new RecommendedTag("a", 0.125, 0.0)
	};
	
	/**
	 * Tests {@link Tag#equals(Object)}.
	 */
	@Test
	public void testEquals() {
		assertFalse(tags[0].equals(tags[1]));
	}
	
	/**
	 * Tests {@link Tag#compareTo(Tag)}.
	 */
	@Test
	public void testCompare1() {
		/*
		 * a < b < c < d
		 * (including transitivity)
		 */
		assertTrue(tags[0].compareTo(tags[1]) > 0);
		assertTrue(tags[0].compareTo(tags[2]) > 0);
		assertTrue(tags[0].compareTo(tags[3]) > 0);
		assertTrue(tags[1].compareTo(tags[2]) > 0);
		assertTrue(tags[1].compareTo(tags[3]) > 0);
		assertTrue(tags[2].compareTo(tags[3]) > 0);
	}
	
	/**
	 * Tests {@link RecommendedTagComparator#compare(RecommendedTag, RecommendedTag)}.
	 */
	@Test
	public void testCompare2() {
		final RecommendedTagComparator comp = new RecommendedTagComparator();
		/* 
		 * We want the tag with the highest score to be first!
		 * 
		 * 1.0 < 0.5 < 0.25 < 0.125
		 * (including transitivity)
		 * 
		 */
		assertTrue(comp.compare(tags[0], tags[1]) < 0);
		assertTrue(comp.compare(tags[0], tags[2]) < 0);
		assertTrue(comp.compare(tags[0], tags[3]) < 0);
		assertTrue(comp.compare(tags[1], tags[2]) < 0);
		assertTrue(comp.compare(tags[1], tags[3]) < 0);
		assertTrue(comp.compare(tags[2], tags[3]) < 0);
	}
	
	
	@Test
	public void testCompare3() {
		final SortedSet<RecommendedTag> testSet = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		
		for (final RecommendedTag t: tags) {
			testSet.add(t);
			/*
			 * tag with highest score should always be first
			 */
			assertEquals(tags[0], testSet.first());
		}
	}
	
	/**
	 * Test that tags with t1.equalsIgnoreCase(t2) get lost.
	 * 
	 * Changes:
	 * - 2009-04-14: case of tags is now ignored!  
	 */
	@Test
	public void testCompare4() {
		final SortedSet<RecommendedTag> testSet = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		
		testSet.add(new RecommendedTag("main", 0.0, 0.0));
		testSet.add(new RecommendedTag("Main", 0.0, 0.0));
		
		assertEquals(1, testSet.size());
		
		assertTrue(testSet.contains(new RecommendedTag("main", 0.0, 0.0)));
		assertTrue(testSet.contains(new RecommendedTag("Main", 0.0, 0.0)));
	}

	
	@Test
	public void testOrder() {
		final SortedSet<RecommendedTag> tags = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		tags.add(new RecommendedTag("eins", 0.3, 0.2));
		tags.add(new RecommendedTag("drei", 0.2, 0.2));
		tags.add(new RecommendedTag("vier", 0.5, 0.2));
		tags.add(new RecommendedTag("sieben", 0.6, 0.2));
		tags.add(new RecommendedTag("eins", 0.5, 0.2));
		tags.add(new RecommendedTag("eins", 0.2, 0.2));
		tags.add(new RecommendedTag("semantic", 0.5, 0.2));
		tags.add(new RecommendedTag("bar", 0.6, 0.2));
		tags.add(new RecommendedTag("foo", 0.7, 0.2));
		tags.add(new RecommendedTag("net", 0.8, 0.2));
		
		final Iterator<RecommendedTag> iterator = tags.iterator();
		
		assertEquals("net", iterator.next().getName());
		assertEquals("foo", iterator.next().getName());
		assertEquals("bar", iterator.next().getName());
		assertEquals("sieben", iterator.next().getName());
		assertEquals("semantic", iterator.next().getName());
		assertEquals("vier", iterator.next().getName());
		assertEquals("eins", iterator.next().getName());
		assertEquals("drei", iterator.next().getName());
		assertFalse(iterator.hasNext());
		
		
	}
	
}

