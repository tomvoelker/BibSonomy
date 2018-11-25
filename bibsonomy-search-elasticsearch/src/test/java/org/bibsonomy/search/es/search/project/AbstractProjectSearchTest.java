package org.bibsonomy.search.es.search.project;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.es.EsSpringContextWrapper;
import org.bibsonomy.search.es.management.ElasticsearchOneToManyManager;
import org.junit.Before;

/**
 * abstract class for {@link org.bibsonomy.model.cris.Project} related tests
 *
 * @author dzo
 */
public abstract class AbstractProjectSearchTest extends AbstractDatabaseManagerTest {

	protected static final ElasticsearchOneToManyManager<Project, Person> PROJECT_MANAGER = (ElasticsearchOneToManyManager<Project, Person>) EsSpringContextWrapper.getContext().getBean("elasticsearchProjectManager");

	protected static final ElasticsearchProjectSearch PROJECT_SEARCH = EsSpringContextWrapper.getContext().getBean(ElasticsearchProjectSearch.class);

	@Before
	public void resetAndRegenerateIndex() throws InterruptedException {
		PROJECT_MANAGER.regenerateAllIndices();

		Thread.sleep(2000);
	}
}
