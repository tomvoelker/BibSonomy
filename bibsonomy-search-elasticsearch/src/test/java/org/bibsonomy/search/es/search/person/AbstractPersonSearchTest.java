package org.bibsonomy.search.es.search.person;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.search.es.EsSpringContextWrapper;
import org.bibsonomy.search.es.management.person.ElasticsearchPersonManager;
import org.junit.Before;

/**
 * abstract class to setup person search related it cases
 *
 * @author dzo
 */
public class AbstractPersonSearchTest extends AbstractDatabaseManagerTest {

	private static final ElasticsearchPersonManager PERSON_SEARCH_MANAGER = EsSpringContextWrapper.getContext().getBean(ElasticsearchPersonManager.class);

	@Before
	public void createIndices() {
		PERSON_SEARCH_MANAGER.regenerateAllIndices();
	}
}
