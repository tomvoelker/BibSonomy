package org.bibsonomy.search.es.testutil;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.es.management.post.ElasticsearchCommunityManager;
import org.junit.Before;

/**
 * abstract class to setup community post search related it cases
 *
 * @author dzo
 */
public abstract class AbstractCommunityPostSearchTest<R extends Resource> extends AbstractDatabaseManagerTest {

	protected abstract ElasticsearchCommunityManager<R> getManager();

	@Before
	public void createIndices() throws InterruptedException {
		this.getManager().regenerateAllIndices();
		// wait for the docs to be indexed by elasticsearch
		Thread.sleep(2000);
	}

	protected void updateIndex() {
		// update both indices
		this.getManager().updateIndex();
		this.getManager().updateIndex();

		// wait some time
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// noop
		}
	}
}
