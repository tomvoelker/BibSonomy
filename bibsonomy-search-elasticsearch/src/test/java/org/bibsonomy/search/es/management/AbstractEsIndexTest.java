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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.es.EsSpringContextWrapper;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.BeanFactory;

/**
 * abstract elasticsearch test
 *
 * @author jensi
 * @author dzo
 */
public abstract class AbstractEsIndexTest extends AbstractDatabaseManagerTest {
	private static final String ELASTICSEARCH_DEFAULT_PATH = "target/elasticsearch-data";
	
	private static Node node;

	/**
	 * start embedded elasticsearch
	 * @throws InterruptedException 
	 */
	@BeforeClass
	public static void initElasticSearch() throws InterruptedException {
		startEmbeddedElasticsearchServer();
		
		Thread.sleep(5 * 1000); // FIXME: just for testing
	}
	
	/**
	 * generates the indices
	 * @throws InterruptedException
	 */
	@Before
	public void createIndices() throws InterruptedException {
		final Map<Class<? extends Resource>, ElasticsearchManager<? extends Resource>> managers = getAllManagers();
		for (ElasticsearchManager<? extends Resource> manager : managers.values()) {
			manager.regenerateAllIndices();
		}
		
		// wait a little bit to get all systems ready TODO: remove?
		Thread.sleep(1000);
	}

	private static void startEmbeddedElasticsearchServer() {
		final Settings.Builder elasticsearchSettings = Settings.settingsBuilder()
				.put("http.port", 9223)
				.put("transport.tcp.port", 9323)
				.put("path.home", ELASTICSEARCH_DEFAULT_PATH);
		
		node = NodeBuilder.nodeBuilder().clusterName("bibsonomy-testcluster")
				.settings(elasticsearchSettings.build()).node();
	}

	/**
	 * @throws IOException
	 */
	@AfterClass
	public static void afterClass() throws IOException {
		closeAllIndices();
		
		node.close();
		
		deleteElasticSearchDataDir(ELASTICSEARCH_DEFAULT_PATH);
	}

	/**
	 * @param elasticsearchDefaultPath
	 * @throws IOException 
	 */
	private static void deleteElasticSearchDataDir(String path) throws IOException {
		final File file = new File(path);
		
		if (file.exists()) {
			FileUtils.deleteDirectory(file);
		}
	}

	private static void closeAllIndices() {
		final Map<Class<? extends Resource>, ElasticsearchManager<? extends Resource>> managers = getAllManagers();
		for (final ElasticsearchManager<?> lrm : managers.values()) {
			lrm.shutdown();
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "resource" })
	private static Map<Class<? extends Resource>, ElasticsearchManager<? extends Resource>> getAllManagers() {
		final BeanFactory bf = EsSpringContextWrapper.getContext();
		return (Map<Class<? extends Resource>, ElasticsearchManager<? extends Resource>>) bf.getBean("elasticsearchManagers");
	}
}
