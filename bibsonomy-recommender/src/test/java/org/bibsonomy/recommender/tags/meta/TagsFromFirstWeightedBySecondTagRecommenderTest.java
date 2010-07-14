package org.bibsonomy.recommender.tags.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.recommender.tags.simple.FixedTagsTagRecommender;
import org.bibsonomy.recommender.tags.simple.SimpleContentBasedTagRecommender;
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
		assertEquals("sieben", iterator.next().getName());
		assertEquals("vier", iterator.next().getName());
		assertEquals("eins", iterator.next().getName());
		assertEquals("drei", iterator.next().getName());
		assertEquals("zwei", iterator.next().getName());
		assertFalse(iterator.hasNext());
	}


	@Test
	public void test2() throws Exception {
		final String[] usersTags = new String[]{"semantic", "web", "social", "net", "graph", "tool", "folksonomy", "holiday"};

		final TagsFromFirstWeightedBySecondTagRecommender merger = new TagsFromFirstWeightedBySecondTagRecommender();
		final SimpleContentBasedTagRecommender simpleContentBasedTagRecommender = new SimpleContentBasedTagRecommender();
		final FixedTagsTagRecommender fixedTagsTagRecommender = new FixedTagsTagRecommender(usersTags);

		merger.setFirstTagRecommender(simpleContentBasedTagRecommender);
		merger.setSecondTagRecommender(fixedTagsTagRecommender);
		merger.setNumberOfTagsToRecommend(5);


		final Bookmark bookmark = new Bookmark();
		bookmark.setTitle("NEPOMUK: the social semantic desktop");

		final Post<Bookmark> post = new Post<Bookmark>();
		post.setResource(bookmark);

		final SortedSet<RecommendedTag> recommendedTags = merger.getRecommendedTags(post);

		/*
		 *  check containment and order of top tags
		 */
		final Iterator<RecommendedTag> iterator = recommendedTags.iterator();
		assertEquals("semantic", iterator.next().getName());
		assertEquals("social", iterator.next().getName());
		assertEquals("nepomuk", iterator.next().getName());
		assertEquals("desktop", iterator.next().getName());
		assertEquals("web", iterator.next().getName());
		assertFalse(iterator.hasNext());
	}

	/**
	 * 
	 * If no tags from first reco are in second reco, we must ensure proper 
	 * scores for fill-up round.
	 * 
	 */
	@Test
	public void testAddRecommendedTags2() {
		final String[] firstFixedTags = new String[]{"eins", "zwei", "drei", "vier", "fünf", "sechs", "sieben", "eins"};
		final SortedSet<RecommendedTag> secondFixedTags = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		secondFixedTags.add(new RecommendedTag("a", 0.3, 0.2));
		secondFixedTags.add(new RecommendedTag("b", 0.2, 0.2));
		secondFixedTags.add(new RecommendedTag("c", 0.5, 0.2));
		secondFixedTags.add(new RecommendedTag("d", 0.6, 0.2));

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
		final RecommendedTag tag1 = iterator.next();
		final double score = tag1.getScore();
		/*
		 * score should be smaller than 1
		 */
		assertTrue(score < 1.0);
		assertEquals("eins", tag1.getName());
		assertEquals("zwei", iterator.next().getName());
		assertEquals("drei", iterator.next().getName());
		assertEquals("vier", iterator.next().getName());
		assertEquals("fünf", iterator.next().getName());
		assertFalse(iterator.hasNext());
	}
}
