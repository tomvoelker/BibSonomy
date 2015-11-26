/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.model.PostWrapper;
import org.junit.Test;

import recommender.core.Recommender;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.util.RecommendationResultComparator;
import recommender.impl.meta.WeightedMergingRecommender;
import recommender.impl.model.RecommendedTag;
import recommender.impl.tags.simple.FixedTagsTagRecommender;


/**
 * Tests the {@link WeightedMergingRecommender} based on the connector's implementation
 * of the recomender's model interface.
 * 
 * @author rja
 */
public class WeightedMergingTagRecommenderTest {

	@Test
	public void testGetRecommendedTags() {
		final WeightedMergingRecommender<TagRecommendationEntity, RecommendedTag> recommender = new WeightedMergingRecommender<TagRecommendationEntity, RecommendedTag>();

		ArrayList<Recommender<TagRecommendationEntity, RecommendedTag>> tagRecommenders = new ArrayList<Recommender<TagRecommendationEntity,RecommendedTag>>(2);
		tagRecommenders.add(new FixedTagsTagRecommender(this.getTags1()));
		tagRecommenders.add(new FixedTagsTagRecommender(this.getTags2()));
		
		recommender.setRecommenders(tagRecommenders);

		recommender.setWeights(new double[] { 0.4, 0.6 });


		final SortedSet<RecommendedTag> recommendedTags = recommender.getRecommendation(new PostWrapper<Bookmark>(this.getPost()));

		assertEquals(recommender.getNumberOfResultsToRecommend(), recommendedTags.size());

		/*
		 * for tag 'web': 0.4 * 0.4 + 0.3 * 0.6 = 0.34
		 */
		assertEquals(0.34, recommendedTags.first().getScore(), 0.001);
	}


	private SortedSet<RecommendedTag> getTags1() {
		final SortedSet<RecommendedTag> result = new TreeSet<RecommendedTag>(new RecommendationResultComparator<RecommendedTag>());

		result.add(new RecommendedTag("semantic",   0.5, 0.1));
		result.add(new RecommendedTag("web",        0.4, 0.1));
		result.add(new RecommendedTag("folksonomy", 0.4, 0.2));
		result.add(new RecommendedTag("holidy",     0.3, 0.5));
		result.add(new RecommendedTag("tree",       0.1, 0.3));

		return result;
	}

	private SortedSet<RecommendedTag> getTags2() {
		final SortedSet<RecommendedTag> result = new TreeSet<RecommendedTag>(new RecommendationResultComparator<RecommendedTag>());

		result.add(new RecommendedTag("semantic", 0.2, 0.1));
		result.add(new RecommendedTag("web",      0.3, 0.1));
		result.add(new RecommendedTag("car",      0.4, 0.2));
		result.add(new RecommendedTag("holiday",  0.2, 0.5));
		result.add(new RecommendedTag("tree",     0.5, 0.3));

		return result;
	}

	private Post<Bookmark> getPost() {
		final Bookmark bookmark = new Bookmark();
		bookmark.setUrl("http://www.example.com/");
		bookmark.setTitle("Eine Beispielseite");

		final Post<Bookmark> post = new Post<Bookmark>();
		post.setResource(bookmark);
		post.setUser(new User("rja"));
		return post;
	}



}
