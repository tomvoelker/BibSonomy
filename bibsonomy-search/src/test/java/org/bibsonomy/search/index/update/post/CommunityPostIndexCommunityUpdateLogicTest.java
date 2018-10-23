package org.bibsonomy.search.index.update.post;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.junit.Test;

import java.util.List;

/**
 * abstract class for {@link CommunityPostIndexCommunityUpdateLogic} tests
 *
 * @param <R>
 * @author dzo
 */
public abstract class CommunityPostIndexCommunityUpdateLogicTest<R extends Resource> extends CommunityPostIndexUpdateLogicTest<R> {

	protected abstract CommunityPostIndexCommunityUpdateLogic<R> getCommunityUpdateLogic();

	@Override
	protected CommunityPostIndexUpdateLogic<R> getUpdateLogic() {
		return this.getCommunityUpdateLogic();
	}

	@Test
	public void testGetNewestPostByInterHash() {
		final Post<R> newestPostByInterHash = this.getCommunityUpdateLogic().getNewestPostByInterHash(this.getPostInterHash());
		this.testNewestPostByInterHash(newestPostByInterHash);
	}

	protected abstract void testNewestPostByInterHash(Post<R> newestPostByInterHash);

	protected abstract String getPostInterHash();

	/**
	 * tests {@link CommunityPostIndexCommunityUpdateLogic#getPostsOfUser(String, int, int)}
	 */
	@Test
	public void testGetPostsOfUser() {
		final List<Post<R>> postsOfUser = this.getCommunityUpdateLogic().getPostsOfUser("testuser1", 10, 0);
		this.testPostsOfUser1(postsOfUser);

		final List<Post<R>> testuser3Posts = this.getCommunityUpdateLogic().getPostsOfUser("testuser3", 10, 0);
		this.testPostsofUser2(testuser3Posts);
	}

	protected abstract void testPostsofUser2(List<Post<R>> testuser3Posts);

	protected abstract void testPostsOfUser1(List<Post<R>> postsOfUser);

	/**
	 * tests {@link CommunityPostIndexCommunityUpdateLogic#getAllPostsOfUser(String)}
	 */
	@Test
	public void testGetAllPostsOfUser() {
		final List<Post<R>> testuser1Posts = this.getCommunityUpdateLogic().getAllPostsOfUser("testuser1");
		this.testAllPostsOfUser1(testuser1Posts);
	}

	protected abstract void testAllPostsOfUser1(List<Post<R>> testuser1Posts);
}
