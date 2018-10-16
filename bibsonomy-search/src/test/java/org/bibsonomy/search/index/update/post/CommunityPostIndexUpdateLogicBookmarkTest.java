package org.bibsonomy.search.index.update.post;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;

import java.util.List;

/**
 * tests for the community bookmarks
 *
 * @author dzo
 */
public class CommunityPostIndexUpdateLogicBookmarkTest extends CommunityPostIndexUpdateLogicTest<GoldStandardBookmark> {
	private static final CommunityPostIndexUpdateLogic<GoldStandardBookmark> UPDATE_LOGIC = (CommunityPostIndexUpdateLogic<GoldStandardBookmark>) SearchSpringContextWrapper.getBeanFactory().getBean("communityBookmarkIndexUpdateLogic");

	@Override
	protected CommunityPostIndexUpdateLogic<GoldStandardBookmark> getUpdateLogic() {
		return UPDATE_LOGIC;
	}

	@Override
	protected void testNewestPostByInterHash(Post<GoldStandardBookmark> newestPostByInterHash) {
		assertThat(newestPostByInterHash.getResource().getTitle(), is("kde"));
	}

	@Override
	protected String getPostInterHash() {
		return "85ab919107e4cc79b345e996b3c0b097";
	}

	@Override
	protected void testNewEntities(List<Post<GoldStandardBookmark>> newCommunityPosts) {
		assertThat(newCommunityPosts.size(), is(1));
		assertThat(newCommunityPosts.get(0).getResource().getTitle(), is("Universität Kassel"));
	}

	@Override
	protected long getLastEntityIdForNewEntities() {
		return 0;
	}

	@Override
	protected void testPostsofUser2(List<Post<GoldStandardBookmark>> testuser3Posts) {
		assertThat(testuser3Posts.size(), is(1));

		assertThat(testuser3Posts.get(0).getResource().getTitle(), is("web.de"));
	}

	@Override
	protected void testPostsOfUser1(List<Post<GoldStandardBookmark>> postsOfUser) {
		assertThat(postsOfUser.size(), is(1));
	}

	@Override
	protected void testAllPostsOfUser1(List<Post<GoldStandardBookmark>> testuser1Posts) {

	}
}
