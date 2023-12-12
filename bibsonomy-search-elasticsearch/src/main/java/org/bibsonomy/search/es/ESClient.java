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
package org.bibsonomy.search.es;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bibsonomy.common.Pair;
import org.bibsonomy.search.es.client.DeleteData;
import org.bibsonomy.search.es.client.IndexData;
import org.bibsonomy.search.es.client.UpdateData;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.Mapping;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;

/**
 * Wrapper around an ElasticSearch Client.
 * 
 * @author lka
 */
public interface ESClient {

	/**
	 * if necessary wait for the index to be ready to work
	 */
	public void waitForReadyState();

	/**
	 * @param indexName
	 * @param alias
	 * @return <code>true</code> iff the index was updated
	 */
	default boolean createAlias(String indexName, String alias) {
		return this.updateAliases(Collections.singleton(new Pair<>(indexName, alias)), Collections.emptySet());
	}

	/**
	 * @param alias
	 * @return the index name of the alias
	 * @throws IllegalStateException if an alias has multiple indices
	 */
	default String getIndexNameForAlias(final String alias) {
		final List<String> activeindices = this.getIndexNamesForAlias(alias);
		if (!activeindices.isEmpty()) {
			if (activeindices.size() > 1) {
				throw new IllegalStateException("found more than one index for this system!");
			}

			return activeindices.iterator().next();
		}
		return null;
	}
	
	/**
	 * @param aliasName
	 * @return a list of index names
	 */
	public List<String> getIndexNamesForAlias(String aliasName);

	boolean insertNewDocument(String indexName, String id, IndexData indexData);

	/**
	 *
	 * @param indexName
	 * @param jsonDocuments
	 * @return <code>true</code> iff all documents were inserted successfully
	 */
	boolean insertNewDocuments(String indexName, Map<String, IndexData> jsonDocuments);
	
	/**
	 * @param indexName
	 * @return <code>true</code> if the index exists on the cluster
	 */
	boolean existsIndexWithName(String indexName);
	
	/**
	 * @param indexName the index containing the search index sync state info
	 * @param syncStateForIndexName the index name of the index
	 * @return
	 */
	<S extends SearchIndexState> S getSearchIndexStateForIndex(String indexName, String syncStateForIndexName, Converter<S, Map<String, Object>, Object> converter);
	
	/**
	 * @param indexName the name of the index
	 * @param mapping the mapping for the type in the index
	 * @param settings the settings to apply
	 * @return
	 */
	boolean createIndex(String indexName, Mapping<XContentBuilder> mapping, String settings);
	
	/**
	 * @param indexName
	 * @return 
	 */
	boolean deleteIndex(String indexName);
	
	/**
	 * Shutdown the ElasticSearch Client. The client will be no more available
	 * for querying and indexing.
	 */
	void shutdown();

	/**
	 * @param aliasesToAdd
	 * @param aliasesToRemove
	 * @return 
	 */
	boolean updateAliases(Set<Pair<String, String>> aliasesToAdd, Set<Pair<String, String>> aliasesToRemove);
	
	default boolean updateOrCreateDocuments(String indexName, Map<String, IndexData> jsonDocuments) {
		if (!present(jsonDocuments)) {
			return true;
		}

		// convert the index data to delete data
		final List<DeleteData> deleteData = jsonDocuments.entrySet().stream().map(entry -> {
			final DeleteData delete = new DeleteData();
			final IndexData indexData = entry.getValue();
			delete.setType(indexData.getType());
			delete.setId(entry.getKey());
			delete.setRouting(indexData.getRouting());
			return delete;
		}).collect(Collectors.toList());

		this.deleteDocuments(indexName, deleteData);
		return this.insertNewDocuments(indexName, jsonDocuments);
	}

	/**
	 * updates the specified document in the index
	 * @param indexName
	 * @param type
	 * @param id
	 * @param jsonDocument
	 * @return <code>true</code> iff the document was updated
	 */
	boolean updateDocument(String indexName, String type, String id, Map<String, Object> jsonDocument);

	/**
	 * @param indexName the index of the documents to update
	 * @param updates the update map (key: document id and value is the update to apply)
	 */
	boolean updateDocuments(String indexName, List<Pair<String, UpdateData>> updates);

	/**
	 * @param indexName
	 * @param alias
	 */
	default void deleteAlias(String indexName, String alias) {
		this.updateAliases(Collections.emptySet(), Collections.singleton(new Pair<>(indexName,alias)));
	}

	/**
	 * @param indexName
	 * @param query
	 * @return the number of documents matching the query in the index
	 */
	long getDocumentCount(String indexName, String type, QueryBuilder query);

	/**
	 *
	 * @param indexName
	 * @param type
	 * @param queryBuilder
	 * @param highlightBuilder
	 * @param orders
	 * @param offset
	 * @param limit
	 * @param minScore
	 * @param fieldsToRetrieve
	 * @return the search hits of the provided query
	 */
	SearchHits search(String indexName, final String type, QueryBuilder queryBuilder, HighlightBuilder highlightBuilder, final List<Pair<String, SortOrder>> orders, int offset, int limit, Float minScore, Set<String> fieldsToRetrieve);

	/**
	 *
	 * @param indexName
	 * @param type
	 * @param queryBuilder
	 * @param aggregationBuilder
	 * @return
	 */
	Aggregations aggregate(String indexName, final String type, QueryBuilder queryBuilder, AggregationBuilder aggregationBuilder);

	/**
	 * @param indexName
	 * @param type
	 * @param query
	 */
	void deleteDocuments(String indexName, String type, QueryBuilder query);
	
	/**
	 * @param indexName
	 * @param documentsToDelete
	 * @return 
	 */
	boolean deleteDocuments(String indexName, List<DeleteData> documentsToDelete);

	/**
	 * Check, if the client is successfully connected to an elasticsearch instance
	 *
	 * @return true, if valid elasticsearch connection
	 */
	boolean isConnected();

	/**
	 * gets the index settings for the specified index
	 * @param indexName
	 * @return the index settings
	 */
	Settings getIndexSettings(String indexName);
}
