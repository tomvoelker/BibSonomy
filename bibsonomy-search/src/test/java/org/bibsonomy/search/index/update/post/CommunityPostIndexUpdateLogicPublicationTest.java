package org.bibsonomy.search.index.update.post;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;

import java.util.List;

/**
 * tests for the community update logic for "normal" publication entries
 *
 * @author dzo
 */
public class CommunityPostIndexUpdateLogicPublicationTest extends CommunityPostIndexCommunityUpdateLogicTest<BibTex> {

	private static final CommunityPostIndexCommunityUpdateLogic<BibTex> UPDATE_LOGIC = (CommunityPostIndexCommunityUpdateLogic<BibTex>) SearchSpringContextWrapper.getBeanFactory().getBean("communityIndexUpdateLogicPublication");

	@Override
	protected CommunityPostIndexCommunityUpdateLogic<BibTex> getUpdateLogic() {
		return UPDATE_LOGIC;
	}

	@Override
	protected void testNewestPostByInterHash(Post<BibTex> newestPostByInterHash) {

	}

	@Override
	protected String getPostInterHash() {
		return null;
	}

	@Override
	protected void testNewEntities(List<Post<BibTex>> newCommunityPosts) {

	}

	@Override
	protected long getLastEntityIdForNewEntities() {
		return 0;
	}

	@Override
	protected void testPostsofUser2(List<Post<BibTex>> testuser3Posts) {

	}

	@Override
	protected void testPostsOfUser1(List<Post<BibTex>> postsOfUser) {

	}

	@Override
	protected void testAllPostsOfUser1(List<Post<BibTex>> testuser1Posts) {

	}
}
