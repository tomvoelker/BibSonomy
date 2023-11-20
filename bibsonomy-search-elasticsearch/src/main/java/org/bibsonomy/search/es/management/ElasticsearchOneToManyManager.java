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
import org.bibsonomy.search.es.index.generator.OneToManyEntityInformationProvider;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.index.update.OneToManyIndexUpdateLogic;
import org.bibsonomy.search.model.SearchIndexDualState;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.util.Converter;

/**
 * Elasticsearch manager for One-Many entity indices
 *
 * @author dzo
 * @param <T>
 * @param <M> the many type
 */
public class ElasticsearchOneToManyManager<T, M> extends ElasticsearchManager<T, SearchIndexDualState> {

	private final OneToManyIndexUpdateLogic<T, M> updateIndexLogic;
	private final DatabaseInformationLogic<SearchIndexDualState> databaseInformationLogic;
	private final OneToManyEntityInformationProvider<T, M> oneToManyEntityInformationProvider;


	/**
	 *
	 * @param systemURI
	 * @param client
	 * @param generator
	 * @param syncStateConverter
	 * @param indexEnabled
	 * @param updateEnabled
	 * @param regenerateEnabled
	 * @param updateIndexLogic
	 * @param databaseInformationLogic
	 * @param oneToManyEntityInformationProvider
	 */
	public ElasticsearchOneToManyManager(URI systemURI,
										 ESClient client,
										 ElasticsearchIndexGenerator<T, SearchIndexDualState> generator,
										 Converter<SearchIndexDualState, Map<String, Object>, Object> syncStateConverter,
										 boolean indexEnabled,
										 boolean updateEnabled,
										 boolean regenerateEnabled,
										 final OneToManyIndexUpdateLogic<T, M> updateIndexLogic,
										 final DatabaseInformationLogic<SearchIndexDualState> databaseInformationLogic,
										 final OneToManyEntityInformationProvider<T, M> oneToManyEntityInformationProvider) {
		super(systemURI, client, generator, syncStateConverter, oneToManyEntityInformationProvider, indexEnabled, updateEnabled, regenerateEnabled);
		this.updateIndexLogic = updateIndexLogic;
		this.databaseInformationLogic = databaseInformationLogic;
		this.oneToManyEntityInformationProvider = oneToManyEntityInformationProvider;
	}

	@Override
	protected void updateIndex(String indexName, SearchIndexDualState oldState) {
		final SearchIndexState oldFirstState = oldState.getFirstState();
		final SearchIndexState oldSecondState = oldState.getSecondState();
		final SearchIndexDualState targetState = this.databaseInformationLogic.getDbState();

		this.updateEntity(indexName, oldFirstState, this.updateIndexLogic.getIndexUpdateLogic(), this.oneToManyEntityInformationProvider);

		// update many relations
		this.updateEntity(indexName, oldSecondState, this.updateIndexLogic.getToManyIndexUpdateLogic(), this.oneToManyEntityInformationProvider.getToManyEntityInformationProvider());

		this.updateIndexState(indexName, oldState, targetState);
	}
}
