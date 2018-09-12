/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.search.es.management;

import java.io.IOException;
import java.util.Map;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.es.EsSpringContextWrapper;
import org.bibsonomy.search.es.management.post.ElasticsearchPostManager;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.AfterClass;
import org.junit.Before;
import org.springframework.beans.factory.BeanFactory;

/**
 * abstract elasticsearch test
 *
 * @author jensi
 * @author dzo
 */
public abstract class AbstractEsIndexTest extends AbstractDatabaseManagerTest {
	
	/**
	 * generates the indices
	 * @throws Exception
	 */
	@Before
	public void createIndices() throws Exception {
		final RestHighLevelClient client = EsSpringContextWrapper.getContext().getBean(RestHighLevelClient.class);

		client.indices().delete(new DeleteIndexRequest("_all"), RequestOptions.DEFAULT);

		final Map<Class<? extends Resource>, ElasticsearchPostManager<? extends Resource>> managers = getAllManagers();
		for (ElasticsearchPostManager<? extends Resource> manager : managers.values()) {
			manager.regenerateAllIndices();
		}
		
		// wait a little bit to get all systems ready TODO: remove?
		Thread.sleep(1000);
	}

	/**
	 * @throws IOException
	 */
	@AfterClass
	public static void afterClass() {
		closeAllIndices();
	}

	private static void closeAllIndices() {
		final Map<Class<? extends Resource>, ElasticsearchPostManager<? extends Resource>> managers = getAllManagers();
		for (final ElasticsearchPostManager<?> lrm : managers.values()) {
			lrm.shutdown();
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "resource" })
	private static Map<Class<? extends Resource>, ElasticsearchPostManager<? extends Resource>> getAllManagers() {
		final BeanFactory bf = EsSpringContextWrapper.getContext();
		return (Map<Class<? extends Resource>, ElasticsearchPostManager<? extends Resource>>) bf.getBean("elasticsearchManagers");
	}
}
