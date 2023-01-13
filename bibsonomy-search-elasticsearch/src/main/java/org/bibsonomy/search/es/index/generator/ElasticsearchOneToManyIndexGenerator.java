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
package org.bibsonomy.search.es.index.generator;

import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.index.generator.OneToManyIndexGenerationLogic;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.util.Converter;

import java.net.URI;
import java.util.Map;

/**
 * generator to generate an entity with a to-many relation
 *
 * @param <T>
 * @param <M>
 */
public class ElasticsearchOneToManyIndexGenerator<T, M, S extends SearchIndexState> extends ElasticsearchIndexGenerator<T, S> {

	private final OneToManyIndexGenerationLogic<T,M> generatorLogic;
	private final OneToManyEntityInformationProvider<T, M> entityInformationProvider;

	/**
	 * default constructor with all required fields
	 *
	 * @param client
	 * @param systemId
	 * @param generationLogic
	 * @param databaseInformationLogic
	 * @param indexSyncStateConverter
	 * @param entityInformationProvider
	 */
	public ElasticsearchOneToManyIndexGenerator(final ESClient client, URI systemId, OneToManyIndexGenerationLogic<T, M> generationLogic, final DatabaseInformationLogic<S> databaseInformationLogic, final Converter<S, Map<String, Object>, Object> indexSyncStateConverter, OneToManyEntityInformationProvider<T, M> entityInformationProvider) {
		super(client, systemId, generationLogic, databaseInformationLogic, indexSyncStateConverter, entityInformationProvider);
		this.generatorLogic = generationLogic;
		this.entityInformationProvider = entityInformationProvider;
	}

	@Override
	protected void insertDataIntoIndex(String indexName) {
		super.insertDataIntoIndex(indexName);

		this.insertDataIntoIndex(indexName, (lastContenId, limit) -> this.generatorLogic.getToManyEntities(lastContenId, limit), this.entityInformationProvider.getToManyEntityInformationProvider(), new IndexVoter<M>());
	}

	@Override
	protected int retrieveNumberOfEntities() {
		return super.retrieveNumberOfEntities() + this.generatorLogic.getNumberOfToManyEntities();
	}
}
