package org.bibsonomy.recommender.tag.simple;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.SortedSet;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.testutil.RecommenderTestContext;
import org.bibsonomy.recommender.tag.model.RecommendedTag;
import org.junit.Test;

/**
 * tests for {@link TagsOfPreviousPostsTagRecommender}
 *
 * @author dzo
 */
public class TagsOfPreviousPostsTagRecommenderTest {
	
	private static TagsOfPreviousPostsTagRecommender RECOMMENDER = RecommenderTestContext.getBeanFactory().getBean(TagsOfPreviousPostsTagRecommender.class);
	
	/**
	 * tests for {@link TagsOfPreviousPostsTagRecommender#getRecommendation(Post)}
	 */
	@Test
	public void testRecommendation() {
		final Post<Resource> post = new Post<>();
		post.setUser(new User("testuser1"));
		final SortedSet<RecommendedTag> tags = RECOMMENDER.getRecommendation(post);
		assertEquals(1, tags.size());
		assertThat(tags, contains(new RecommendedTag("weltmeisterschaft", 0, 0)));
	}
}
