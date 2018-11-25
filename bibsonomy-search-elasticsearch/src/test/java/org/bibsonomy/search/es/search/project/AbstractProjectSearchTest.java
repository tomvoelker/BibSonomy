package org.bibsonomy.search.es.search.project;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.search.es.EsSpringContextWrapper;
import org.bibsonomy.search.es.management.project.ElasticsearchProjectManager;
import org.junit.Before;

/**
 * abstract class for {@link org.bibsonomy.model.cris.Project} related tests
 *
 * @author dzo
 */
public abstract class AbstractProjectSearchTest extends AbstractDatabaseManagerTest {

	protected static final ElasticsearchProjectManager PROJECT_MANAGER = EsSpringContextWrapper.getContext().getBean(ElasticsearchProjectManager.class);

	protected static final ElasticsearchProjectSearch PROJECT_SEARCH = EsSpringContextWrapper.getContext().getBean(ElasticsearchProjectSearch.class);

	@Before
	public void resetAndRegenerateIndex() {
		PROJECT_MANAGER.regenerateAllIndices();
	}

	protected void updateIndex() {
		PROJECT_MANAGER.updateIndex();
		PROJECT_MANAGER.updateIndex();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// ignore
		}
	}
}
