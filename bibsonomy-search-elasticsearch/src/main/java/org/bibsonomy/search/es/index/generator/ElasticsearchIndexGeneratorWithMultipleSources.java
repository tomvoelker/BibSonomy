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
import org.bibsonomy.search.index.generator.IndexGenerationLogic;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.util.Converter;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * uses n generator sources to generate an index
 *
 * @param <E> the entity class
 *
 * @author dzo
 */
public class ElasticsearchIndexGeneratorWithMultipleSources<E, S extends SearchIndexState> extends ElasticsearchIndexGenerator<E, S> {

	private final List<IndexGenerationLogic<E>> generationLogics;

	/**
	 * default constructor with all required fields
	 *
	 * @param client                    the client to use
	 * @param systemId
	 * @param generationLogics          the generator logics to use
	 * @param databaseInformationLogic
	 * @param indexSyncStateConverter
	 * @param entityInformationProvider the entity information provider for this entity index@param entityInformationProvider the entity information provider for this entity index
	 * @param generationLogics
	 */
	public ElasticsearchIndexGeneratorWithMultipleSources(ESClient client, URI systemId, DatabaseInformationLogic<S> databaseInformationLogic, Converter<S, Map<String, Object>, Object> indexSyncStateConverter, EntityInformationProvider<E> entityInformationProvider, List<IndexGenerationLogic<E>> generationLogics) {
		super(client, systemId, generationLogics.get(0), databaseInformationLogic, indexSyncStateConverter, entityInformationProvider);
		this.generationLogics = generationLogics;
	}

	@Override
	public void insertDataIntoIndex(final String indexName) {
		for (final IndexGenerationLogic<E> generationLogic : this.generationLogics) {
			this.insertDataIntoIndex(indexName, generationLogic::getEntities, this.entityInformationProvider, new IndexVoter<E>());
		}
	}

	@Override
	protected int retrieveNumberOfEntities() {
		return this.generationLogics.stream().map(IndexGenerationLogic::getNumberOfEntities).mapToInt(Integer::intValue).sum();
	}
}
