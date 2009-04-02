package org.bibsonomy.recommender.tags.meta;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Assert;

import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.recommender.tags.simple.FixedTagsTagRecommender;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class TagsFromFirstWeightedBySecondTagRecommenderTest {

	@Test
	public void testAddRecommendedTags() {
		final String[] firstFixedTags = new String[]{"eins", "zwei", "drei", "vier", "fünf", "sechs", "sieben", "eins"};
		final SortedSet<RecommendedTag> secondFixedTags = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		secondFixedTags.add(new RecommendedTag("eins", 0.3, 0.2));
		secondFixedTags.add(new RecommendedTag("drei", 0.2, 0.2));
		secondFixedTags.add(new RecommendedTag("vier", 0.5, 0.2));
		secondFixedTags.add(new RecommendedTag("sieben", 0.6, 0.2));
		secondFixedTags.add(new RecommendedTag("eins", 0.5, 0.2));
		secondFixedTags.add(new RecommendedTag("eins", 0.2, 0.2));
		secondFixedTags.add(new RecommendedTag("semantic", 0.5, 0.2));
		secondFixedTags.add(new RecommendedTag("bar", 0.6, 0.2));
		secondFixedTags.add(new RecommendedTag("foo", 0.7, 0.2));
		secondFixedTags.add(new RecommendedTag("net", 0.8, 0.2));
		

		final FixedTagsTagRecommender first = new FixedTagsTagRecommender(firstFixedTags);
		final FixedTagsTagRecommender second = new FixedTagsTagRecommender(secondFixedTags);
		final TagsFromFirstWeightedBySecondTagRecommender merger = new TagsFromFirstWeightedBySecondTagRecommender();
		
		merger.setFirstTagRecommender(first);
		merger.setSecondTagRecommender(second);
		merger.setNumberOfTagsToRecommend(5);
		
		final SortedSet<RecommendedTag> recommendedTags = merger.getRecommendedTags(null);
		
		/*
		 *  check containment and order of top tags
		 */
		final Iterator<RecommendedTag> iterator = recommendedTags.iterator();
		Assert.assertEquals("sieben", iterator.next().getName());
		Assert.assertEquals("vier", iterator.next().getName());
		Assert.assertEquals("eins", iterator.next().getName());
		Assert.assertEquals("drei", iterator.next().getName());
		Assert.assertEquals("net", iterator.next().getName());
		Assert.assertFalse(iterator.hasNext());
		
		
		
	}

}
