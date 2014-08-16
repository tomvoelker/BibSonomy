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
