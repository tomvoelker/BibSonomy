/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.recommender.connector.tags.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.recommender.connector.model.PostWrapper;
import org.junit.Test;

import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.util.RecommendationResultComparator;
import recommender.impl.meta.ResultsFromFirstWeightedBySecondFilledByThirdRecommender;
import recommender.impl.model.RecommendedTag;
import recommender.impl.tags.simple.FixedTagsTagRecommender;
import recommender.impl.tags.simple.SimpleContentBasedTagRecommender;

/**
 * Tests the {@link ResultsFromFirstWeightedBySecondFilledByThirdRecommender} based on the connector's implementation
 * of the recomender's model interface.
 * 
 * @author rja
 */
public class TagsFromFirstWeightedBySecondFilledByThirdTagRecommenderTest {

	@Test
	public void testAddRecommendedTags() {
		final String[] firstFixedTags = new String[]{"eins", "zwei", "drei", "vier", "fünf", "sechs", "sieben", "eins"};
		final SortedSet<RecommendedTag> secondFixedTags = new TreeSet<RecommendedTag>(new RecommendationResultComparator<RecommendedTag>());
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


		final ResultsFromFirstWeightedBySecondFilledByThirdRecommender<TagRecommendationEntity, RecommendedTag> merger = new ResultsFromFirstWeightedBySecondFilledByThirdRecommender<TagRecommendationEntity, RecommendedTag>();

		merger.setFirstRecommender(new FixedTagsTagRecommender(firstFixedTags));
		merger.setSecondRecommender(new FixedTagsTagRecommender(secondFixedTags));
		merger.setThirdRecommender(new FixedTagsTagRecommender(secondFixedTags));
		merger.setNumberOfResultsToRecommend(5);

		final SortedSet<RecommendedTag> recommendedTags = merger.getRecommendation(null);


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
	public void test2() {
		final String[] usersTags = new String[]{"semantic", "web", "social", "net", "graph", "tool", "folksonomy", "holiday"};

		final ResultsFromFirstWeightedBySecondFilledByThirdRecommender<TagRecommendationEntity, RecommendedTag> merger = new ResultsFromFirstWeightedBySecondFilledByThirdRecommender<TagRecommendationEntity, RecommendedTag>();
		final SimpleContentBasedTagRecommender simpleContentBasedTagRecommender = new SimpleContentBasedTagRecommender();
		final FixedTagsTagRecommender fixedTagsTagRecommender = new FixedTagsTagRecommender(usersTags);

		merger.setFirstRecommender(simpleContentBasedTagRecommender);
		merger.setSecondRecommender(fixedTagsTagRecommender);
		merger.setThirdRecommender(fixedTagsTagRecommender);
		merger.setNumberOfResultsToRecommend(5);


		final Bookmark bookmark = new Bookmark();
		bookmark.setTitle("NEPOMUK: the social semantic desktop");

		final Post<Bookmark> post = new Post<Bookmark>();
		post.setResource(bookmark);

		final SortedSet<RecommendedTag> recommendedTags = merger.getRecommendation(new PostWrapper<Bookmark>(post));

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


	@Test
	public void test3() {
		final String[] usersTags = new String[]{"semantic", "web", "social", "net", "graph", "tool", "folksonomy", "holiday"};
		final String[] resourceTags = new String[]{"project"};

		final ResultsFromFirstWeightedBySecondFilledByThirdRecommender<TagRecommendationEntity, RecommendedTag> merger = new ResultsFromFirstWeightedBySecondFilledByThirdRecommender<TagRecommendationEntity, RecommendedTag>();
		final SimpleContentBasedTagRecommender simpleContentBasedTagRecommender = new SimpleContentBasedTagRecommender();
		final FixedTagsTagRecommender fixedTagsTagRecommender = new FixedTagsTagRecommender(usersTags);
		final FixedTagsTagRecommender fillupTagRecommender = new FixedTagsTagRecommender(resourceTags);

		merger.setFirstRecommender(simpleContentBasedTagRecommender);
		merger.setSecondRecommender(fixedTagsTagRecommender);
		merger.setThirdRecommender(fillupTagRecommender);
		merger.setNumberOfResultsToRecommend(5);


		final Bookmark bookmark = new Bookmark();
		bookmark.setTitle("NEPOMUK: the social semantic desktop");

		final Post<Bookmark> post = new Post<Bookmark>();
		post.setResource(bookmark);

		final SortedSet<RecommendedTag> recommendedTags = merger.getRecommendation(new PostWrapper<Bookmark>(post));

		/*
		 *  check containment and order of top tags
		 */
		final Iterator<RecommendedTag> iterator = recommendedTags.iterator();
		assertEquals("semantic", iterator.next().getName());
		assertEquals("social", iterator.next().getName());
		assertEquals("nepomuk", iterator.next().getName());
		assertEquals("desktop", iterator.next().getName());
		assertEquals("project", iterator.next().getName());
		assertFalse(iterator.hasNext());
	}


}
