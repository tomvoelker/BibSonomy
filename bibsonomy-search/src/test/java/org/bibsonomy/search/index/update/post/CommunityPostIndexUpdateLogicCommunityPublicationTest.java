package org.bibsonomy.search.index.update.post;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;

import java.util.List;

/**
 * tests for {@link CommunityPostIndexCommunityUpdateLogic}
 *
 * @author dzo
 */
public class CommunityPostIndexUpdateLogicCommunityPublicationTest extends CommunityPostIndexCommunityUpdateLogicTest<GoldStandardPublication> {

	private static final CommunityPostIndexCommunityUpdateLogic<GoldStandardPublication> UPDATE_LOGIC = (CommunityPostIndexCommunityUpdateLogic<GoldStandardPublication>) SearchSpringContextWrapper.getBeanFactory().getBean("communityIndexUpdateLogicCommunityPublication");

	@Override
	protected CommunityPostIndexCommunityUpdateLogic<GoldStandardPublication> getCommunityUpdateLogic() {
		return UPDATE_LOGIC;
	}

	@Override
	protected void testNewestPostByInterHash(Post<GoldStandardPublication> newestPostByInterHash) {
		assertThat(newestPostByInterHash.getResource().getTitle(), is("A case for abductive reasoning over ontologies"));
	}

	@Override
	protected String getPostInterHash() {
		return "a5936835f9eeab91eb09d84948306178";
	}

	@Override
	protected void testNewEntities(List<Post<GoldStandardPublication>> newCommunityPosts) {
		assertThat(newCommunityPosts.size(), is(1));
		assertThat(newCommunityPosts.get(0).getResource().getTitle(), is("Wurst aufs Brot"));
	}

	@Override
	protected long getLastEntityIdForNewEntities() {
		return 1073740826;
	}

	@Override
	protected void testPostsofUser2(List<Post<GoldStandardPublication>> testuser3Posts) {
		assertThat(testuser3Posts.size(), is(1));

		assertThat(testuser3Posts.get(0).getResource().getTitle(), is("test friend title"));
	}

	@Override
	protected void testPostsOfUser1(List<Post<GoldStandardPublication>> postsOfUser) {
		assertThat(postsOfUser.size(), is(0)); // testuser1 has one public post but there is a gold standard for this post
	}

	@Override
	protected void testAllPostsOfUser1(List<Post<GoldStandardPublication>> testuser1Posts) {
		assertThat(testuser1Posts.size(), is(1));
	}
}
