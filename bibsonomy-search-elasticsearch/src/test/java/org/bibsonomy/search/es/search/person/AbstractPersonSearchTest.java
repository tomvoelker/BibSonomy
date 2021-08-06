/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
