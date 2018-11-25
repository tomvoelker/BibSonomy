package org.bibsonomy.search.es.search.person;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.search.es.EsSpringContextWrapper;
import org.bibsonomy.search.es.management.ElasticsearchOneToManyManager;
import org.junit.Before;

/**
 * abstract class to setup person search related it cases
 *
 * @author dzo
 */
public class AbstractPersonSearchTest extends AbstractDatabaseManagerTest {

	protected static final ElasticsearchOneToManyManager<Person, ResourcePersonRelation> PERSON_SEARCH_MANAGER = (ElasticsearchOneToManyManager<Person, ResourcePersonRelation>) EsSpringContextWrapper.getContext().getBean("elasticsearchPersonManager");

	/** for checking the update */
	protected static final ElasticsearchPersonSearch PERSON_SEARCH = EsSpringContextWrapper.getContext().getBean(ElasticsearchPersonSearch.class);

	@Before
	public void createIndices() throws InterruptedException {
		PERSON_SEARCH_MANAGER.regenerateAllIndices();
		// wait for the docs to be indexed by elasticsearch
		Thread.sleep(2000);
	}
}
