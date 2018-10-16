package org.bibsonomy.search.index.update.post;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * abstract class for {@link CommunityPostIndexUpdateLogic} tests
 *
 * @param <R>
 */
public abstract class CommunityPostIndexUpdateLogicTest<R extends Resource> extends AbstractDatabaseManagerTest {

	protected abstract CommunityPostIndexUpdateLogic<R> getUpdateLogic();

	@Test
	public void testGetNewestPostByInterHash() {
		final Post<R> newestPostByInterHash = this.getUpdateLogic().getNewestPostByInterHash(this.getPostInterHash());
		this.testNewestPostByInterHash(newestPostByInterHash);
	}

	protected abstract void testNewestPostByInterHash(Post<R> newestPostByInterHash);

	protected abstract String getPostInterHash();

	/**
	 * tests {@link CommunityPostIndexUpdateLogic#getDeletedEntities(Date)}
	 */
	@Test
	public void testGetDeletedEntities() {
		final Date lastLogDate = new Date();
		final List<Post<R>> deletedEntities = this.getUpdateLogic().getDeletedEntities(lastLogDate);
		assertThat(deletedEntities.size(), is(0));
	}

	/**
	 * tests {@link CommunityPostIndexUpdateLogic#getNewerEntities(long, Date, int, int)}
	 */
	@Test
	public void testGetNewEntities() {
		final List<Post<R>> newCommunityPosts = this.getUpdateLogic().getNewerEntities(this.getLastEntityIdForNewEntities(), new Date(), 1, 0);
		assertThat(newCommunityPosts.size(), is(1));
		this.testNewEntities(newCommunityPosts);
	}

	protected abstract void testNewEntities(List<Post<R>> newCommunityPosts);

	protected abstract long getLastEntityIdForNewEntities();

	/**
	 * tests {@link CommunityPostIndexUpdateLogic#getPostsOfUser(String, int, int)}
	 */
	@Test
	public void testGetPostsOfUser() {
		final List<Post<R>> postsOfUser = this.getUpdateLogic().getPostsOfUser("testuser1", 10, 0);
		this.testPostsOfUser1(postsOfUser);

		final List<Post<R>> testuser3Posts = this.getUpdateLogic().getPostsOfUser("testuser3", 10, 0);
		this.testPostsofUser2(testuser3Posts);
	}

	protected abstract void testPostsofUser2(List<Post<R>> testuser3Posts);

	protected abstract void testPostsOfUser1(List<Post<R>> postsOfUser);

	/**
	 * tests {@link CommunityPostIndexUpdateLogic#getAllPostsOfUser(String)}
	 */
	@Test
	public void testGetAllPostsOfUser() {
		final List<Post<R>> testuser1Posts = this.getUpdateLogic().getAllPostsOfUser("testuser1");
		this.testAllPostsOfUser1(testuser1Posts);
	}

	protected abstract void testAllPostsOfUser1(List<Post<R>> testuser1Posts);
}
