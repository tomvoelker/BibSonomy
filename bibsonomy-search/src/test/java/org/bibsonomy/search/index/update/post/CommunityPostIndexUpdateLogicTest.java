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
 * abstract test class for {@link CommunityPostIndexUpdateLogic}
 * @param <R>
 */
public abstract class CommunityPostIndexUpdateLogicTest<R extends Resource> extends AbstractDatabaseManagerTest {

	protected abstract CommunityPostIndexUpdateLogic<R> getUpdateLogic();

	/**
	 * tests {@link CommunityPostIndexCommunityUpdateLogic#getNewerEntities(long, Date, int, int)}
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
	 * tests {@link CommunityPostIndexCommunityUpdateLogic#getDeletedEntities(Date)}
	 */
	@Test
	public void testGetDeletedEntities() {
		final Date lastLogDate = new Date();
		final List<Post<R>> deletedEntities = this.getUpdateLogic().getDeletedEntities(lastLogDate);
		assertThat(deletedEntities.size(), is(0));
	}

}
