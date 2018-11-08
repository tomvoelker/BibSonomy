package org.bibsonomy.search.index.generator.post;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.junit.Test;

import java.util.List;

/**
 * abstract test
 *
 * @author dzo
 */
public abstract class CommunityPostIndexGenerationLogicTest<R extends Resource> extends AbstractDatabaseManagerTest {

	/**
	 * finds the first post with the specified interhash in a list of posts
	 * @param entities
	 * @param hash
	 * @param <R>
	 * @return
	 */
	protected static <R extends Resource> Post<R> getFirstPostByInterHash(List<Post<R>> entities, String hash) {
		return entities.stream().filter(post -> post.getResource().getInterHash().equals(hash)).findFirst().get();
	}

	/**
	 * @return the resource logic to test
	 */
	protected abstract CommunityPostIndexGenerationLogic<R> getLogic();

	@Test
	public void testGetNumberOfEntities() {
		final int numberOfEntities = this.getLogic().getNumberOfEntities();
		assertThat(numberOfEntities, is(this.getNumberOfEntites()));
	}

	/**
	 * @return the number of entities in the database
	 */
	protected abstract int getNumberOfEntites();

	@Test
	public void testGetEntities() {
		final List<Post<R>> entities = this.getLogic().getEntites(0, this.getNumberOfEntites());
		assertThat(entities.size(), is(this.getNumberOfEntites()));
		this.testEntities(entities);
	}

	protected abstract void testEntities(List<Post<R>> entities);
}
