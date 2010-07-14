package org.bibsonomy.recommender.tags.meta;

import static org.junit.Assert.assertEquals;

import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.recommender.tags.simple.FixedTagsTagRecommender;
import org.bibsonomy.services.recommender.TagRecommender;
import org.junit.Test;


/**
 * @author rja
 * @version $Id$
 */
public class WeightedMergingTagRecommenderTest {

	@Test
	public void testGetRecommendedTags() {
		final WeightedMergingTagRecommender recommender = new WeightedMergingTagRecommender();

		recommender.setTagRecommenders(new TagRecommender[] 
		                                                  {
				new FixedTagsTagRecommender(getTags1()),
				new FixedTagsTagRecommender(getTags2())
		                                                  }
		);

		recommender.setWeights(new double[] { 0.4, 0.6 });


		final SortedSet<RecommendedTag> recommendedTags = recommender.getRecommendedTags(getPost());

		assertEquals(recommender.getNumberOfTagsToRecommend(), recommendedTags.size());

		/*
		 * for tag 'web': 0.4 * 0.4 + 0.3 * 0.6 = 0.34
		 */
		assertEquals(0.34, recommendedTags.first().getScore());
	}


	private SortedSet<RecommendedTag> getTags1() {
		final SortedSet<RecommendedTag> result = new TreeSet<RecommendedTag>(new RecommendedTagComparator());

		result.add(new RecommendedTag("semantic",   0.5, 0.1));
		result.add(new RecommendedTag("web",        0.4, 0.1));
		result.add(new RecommendedTag("folksonomy", 0.4, 0.2));
		result.add(new RecommendedTag("holidy",     0.3, 0.5));
		result.add(new RecommendedTag("tree",       0.1, 0.3));

		return result;
	}

	private SortedSet<RecommendedTag> getTags2() {
		final SortedSet<RecommendedTag> result = new TreeSet<RecommendedTag>(new RecommendedTagComparator());

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
