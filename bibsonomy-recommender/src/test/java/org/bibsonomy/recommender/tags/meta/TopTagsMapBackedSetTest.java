package org.bibsonomy.recommender.tags.meta;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Assert;

import org.bibsonomy.model.RecommendedTag;
import org.junit.Test;


/**
 * @author rja
 * @version $Id$
 */
public class TopTagsMapBackedSetTest {

	
	@Test
	public void test1() throws Exception {
		final Set<RecommendedTag> tempSet = new TreeSet<RecommendedTag>(); // all tags
		final Set<RecommendedTag> topSet = new TreeSet<RecommendedTag>();  // top tags
		
		tempSet.add(new RecommendedTag("a", 0.1, 0.5));
		tempSet.add(new RecommendedTag("b", 0.2, 0.5));
		tempSet.add(new RecommendedTag("c", 0.3, 0.5));
		
		topSet.add(new RecommendedTag("d", 0.4, 0.5));
		topSet.add(new RecommendedTag("e", 0.5, 0.5));
		topSet.add(new RecommendedTag("f", 0.6, 0.5));
		topSet.add(new RecommendedTag("g", 0.7, 0.5));
		topSet.add(new RecommendedTag("h", 0.8, 0.5));
		

		tempSet.addAll(topSet);
		
		final TopTagsMapBackedSet set = new TopTagsMapBackedSet(5);
		for (final RecommendedTag recommendedTag : tempSet) {
			set.add(recommendedTag);
		}
		
		/*
		 * sets should be equal, basically ...
		 */
		Assert.assertTrue(set.containsAll(tempSet));
		Assert.assertTrue(tempSet.containsAll(set));
		
		/*
		 * we should get the top five tags ...
		 */
		final SortedSet<RecommendedTag> sortedTags = set.getTopTags();
		Assert.assertEquals(5, sortedTags.size());
		
		Assert.assertTrue(topSet.containsAll(sortedTags));
		Assert.assertTrue(sortedTags.containsAll(topSet));
	}
	
}
