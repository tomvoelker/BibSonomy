package org.bibsonomy.recommender.tags.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class FixedTagsTagRecommenderTest {

	/**
	 * Checks, if exactly those tags given in the constructor are returned as recommendation.
	 */
	@Test
	public void testFixedTagsTagRecommenderStringArray() {
		final String[] fixedTags = new String[]{"eins", "zwei", "drei", "vier", "fünf", "sechs", "sieben", "eins"};

		final FixedTagsTagRecommender recommender = new FixedTagsTagRecommender(fixedTags);

		/*
		 * we compare only the names of the tags and disregard their order.
		 */
		final SortedSet<RecommendedTag> recommendedTags = new TreeSet<RecommendedTag>(new Comparator<RecommendedTag>() {
			@Override
			public int compare(final RecommendedTag o1, final RecommendedTag o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		recommender.addRecommendedTags(recommendedTags, null);

		for (final String tag: fixedTags) {
			assertTrue(recommendedTags.contains(new RecommendedTag(tag, 0.0, 0.0)));
		}
	}

	/**
	 * Checks, if exactly those tags given in the constructor are returned as recommendation.
	 */
	@Test
	public void testFixedTagsTagRecommenderSortedSetOfRecommendedTag() {
		final SortedSet<RecommendedTag> recommendedTags = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		recommendedTags.add(new RecommendedTag("eins", 0.3, 0.2));
		recommendedTags.add(new RecommendedTag("drei", 0.2, 0.2));
		recommendedTags.add(new RecommendedTag("vier", 0.5, 0.2));
		recommendedTags.add(new RecommendedTag("sieben", 0.6, 0.2));
		recommendedTags.add(new RecommendedTag("eins", 0.5, 0.2));
		recommendedTags.add(new RecommendedTag("eins", 0.2, 0.2));
		recommendedTags.add(new RecommendedTag("semantic", 0.5, 0.2));
		recommendedTags.add(new RecommendedTag("bar", 0.6, 0.2));
		recommendedTags.add(new RecommendedTag("foo", 0.7, 0.2));
		recommendedTags.add(new RecommendedTag("net", 0.8, 0.2));
		final FixedTagsTagRecommender recommender = new FixedTagsTagRecommender(recommendedTags);
		assertEquals(recommendedTags, recommender.getRecommendedTags(null));
	}

}
