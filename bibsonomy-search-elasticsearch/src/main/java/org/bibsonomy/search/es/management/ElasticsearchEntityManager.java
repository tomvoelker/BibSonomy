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
package org.bibsonomy.search.es.management;

import java.net.URI;
import java.util.Map;

import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.util.Converter;

/**
 * Elasticsearch manager for single entity indices
 *
 * @author dzo
 */
public class ElasticsearchEntityManager<T> extends ElasticsearchManager<T, DefaultSearchIndexSyncState> {

	private final DatabaseInformationLogic<DefaultSearchIndexSyncState> databaseInformationLogic;
	private final IndexUpdateLogic<T> updateIndexLogic;

	/**
	 * Default constructor
	 *
	 * @param systemURI
	 * @param client
	 * @param generator
	 * @param syncStateConverter
	 * @param entityInformationProvider
	 * @param indexEnabled
	 * @param updateEnabled
	 * @param regenerateEnabled
	 * @param databaseInformationLogic
	 * @param updateIndexLogic
	 */
	public ElasticsearchEntityManager(URI systemURI,
									  ESClient client,
									  ElasticsearchIndexGenerator<T, DefaultSearchIndexSyncState> generator,
									  Converter<DefaultSearchIndexSyncState, Map<String, Object>, Object> syncStateConverter,
									  EntityInformationProvider<T> entityInformationProvider,
									  boolean indexEnabled,
									  boolean updateEnabled,
									  boolean regenerateEnabled,
									  DatabaseInformationLogic<DefaultSearchIndexSyncState> databaseInformationLogic,
									  IndexUpdateLogic<T> updateIndexLogic) {
		super(systemURI, client, generator, syncStateConverter, entityInformationProvider, indexEnabled, updateEnabled, regenerateEnabled);
		this.databaseInformationLogic = databaseInformationLogic;
		this.updateIndexLogic = updateIndexLogic;
	}

	@Override
	protected void updateIndex(String indexName, DefaultSearchIndexSyncState oldState) {
		final DefaultSearchIndexSyncState targetState = this.databaseInformationLogic.getDbState();

		this.updateEntity(indexName, targetState, this.updateIndexLogic, this.entityInformationProvider);

		this.updateIndexState(indexName, oldState, targetState);
	}
}
