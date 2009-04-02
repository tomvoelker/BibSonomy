/*
 * Created on 08.04.2006
 */
package org.bibsonomy.recommender.tags.simple.termprocessing;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

public class TermProcessingIteratorTest extends TestCase {
	
	public void testIt1() {
		final Collection<String> words = Arrays.asList(new String[] {"it","der","Bibsonomy","the","než","\u0438\u0437"});
		Iterator<String> it = new TermProcessingIterator(words.iterator());
		assertTrue( it.hasNext() );
		assertEquals("bibsonomy",it.next());
		assertFalse( it.hasNext() );
	}
	
	public void testIt2() {
		final Collection<String> words = Arrays.asList(new String[] {"it","der","Bibsonomy","the","než","\u0438\u0437", "foo3BaR.."});
		Iterator<String> it = new TermProcessingIterator(words.iterator());
		assertTrue( it.hasNext() );
		assertEquals("bibsonomy",it.next());
		assertTrue( it.hasNext() );
		assertEquals("foo3bar",it.next());
		assertFalse( it.hasNext() );
	}
}
