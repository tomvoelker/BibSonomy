package org.bibsonomy.recommender;

import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.RecommendedTagComparator;
import org.bibsonomy.model.Tag;
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
	 * Test that tags with t1.equalsIgnoreCase(t2) don't get lost.
	 */
	@Test
	public void testCompare4() {
		final SortedSet<RecommendedTag> testSet = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		
		testSet.add(new RecommendedTag("main", 0.0, 0.0));
		testSet.add(new RecommendedTag("Main", 0.0, 0.0));
		
		assertEquals(2, testSet.size());
		
		assertTrue(testSet.contains(new RecommendedTag("main", 0.0, 0.0)));
		assertTrue(testSet.contains(new RecommendedTag("Main", 0.0, 0.0)));
	}

}
