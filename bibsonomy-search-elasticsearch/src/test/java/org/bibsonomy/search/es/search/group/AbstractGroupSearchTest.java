package org.bibsonomy.search.es.search.group;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Group;
import org.bibsonomy.search.es.EsSpringContextWrapper;
import org.bibsonomy.search.es.management.ElasticsearchEntityManager;
import org.junit.Before;

/**
 * abstract group search test case
 *
 * @author dzo
 */
public class AbstractGroupSearchTest extends AbstractDatabaseManagerTest {

	protected static final ElasticsearchEntityManager<Group> GROUP_SEARCH_MANAGER = EsSpringContextWrapper.getContext().getBean("elasticsearchGroupManager", ElasticsearchEntityManager.class);

	/** for checking the update */
	protected static final ElasticsearchGroupSearch GROUP_SEARCH = EsSpringContextWrapper.getContext().getBean(ElasticsearchGroupSearch.class);

	@Before
	public void createIndices() throws InterruptedException {
		GROUP_SEARCH_MANAGER.regenerateAllIndices();
		// wait for the docs to be indexed by elasticsearch
		Thread.sleep(2000);
	}
}
