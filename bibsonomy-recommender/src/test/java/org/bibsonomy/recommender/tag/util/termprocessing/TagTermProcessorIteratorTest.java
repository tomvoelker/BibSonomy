/**
 * BibSonomy Recommendation - Tag and resource recommender.
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
package org.bibsonomy.recommender.tag.util.termprocessing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

/**
 * @author dzo
 */
public class TagTermProcessorIteratorTest {
	
	
	@Test
	public void testNext() {
		final TagTermProcessorIterator iterator = new TagTermProcessorIterator(Arrays.asList("i", "have", "imported", "my", "bookmark", "collection").iterator());
		
		assertEquals("bookmark", iterator.next());
		assertEquals("collection", iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	@Test
	public void testIt1() {
		final Collection<String> words = Arrays.asList("it","der","Bibsonomy","the","než","\u0438\u0437");
		final Iterator<String> it = new TagTermProcessorIterator(words.iterator());
		assertTrue( it.hasNext() );
		assertEquals("bibsonomy",it.next());
		assertFalse( it.hasNext() );
	}

	@Test
	public void testIt2() {
		final Collection<String> words = Arrays.asList("it","der","Bibsonomy","the","než","\u0438\u0437", "foo3BaR..");
		final Iterator<String> it = new TagTermProcessorIterator(words.iterator());
		assertTrue( it.hasNext() );
		assertEquals("bibsonomy",it.next());
		assertTrue( it.hasNext() );
		assertEquals("foo3bar",it.next());
		assertFalse( it.hasNext() );
	}
}
