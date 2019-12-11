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
package org.bibsonomy.search.es;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.SortOrder;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.search.util.Mapping;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

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

	/**
	 * @param indexName
	 * @param type
	 * @param id
	 * @param jsonDocument
	 * @return <code>true</code> iff the document was inserted successfully
	 */
	public boolean insertNewDocument(String indexName, String type, String id, Map<String, Object> jsonDocument);
	
	/**
	 * 
	 * @param indexName
	 * @param type
	 * @param jsonDocuments
	 * @return <code>true</code> iff all documents were inserted successfully
	 */
	public boolean insertNewDocuments(String indexName, String type, Map<String, Map<String, Object>> jsonDocuments);
	
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
	SearchIndexSyncState getSearchIndexStateForIndex(String indexName, String syncStateForIndexName);
	
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
	
	default boolean updateOrCreateDocuments(String indexName, String type, Map<String, Map<String, Object>> jsonDocuments) {
		final Set<String> idsToDelete = jsonDocuments.keySet();
		this.deleteDocuments(indexName, type, idsToDelete);
		return this.insertNewDocuments(indexName, type, jsonDocuments);
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
	 * @param sortOrder
	 * @param offset
	 * @param limit
	 * @param minScore
	 * @param fieldsToRetrieve
	 * @return the search hits of the provided query
	 */
	SearchHits search(String indexName, final String type, QueryBuilder queryBuilder, HighlightBuilder highlightBuilder, SortOrder sortOrder, int offset, int limit, Float minScore, Set<String> fieldsToRetrieve);

	/**
	 * @param indexName
	 * @param type
	 * @param query
	 */
	public void deleteDocuments(String indexName, String type, QueryBuilder query);
	
	/**
	 * @param indexName
	 * @param type
	 * @param idsToDelete
	 * @return 
	 */
	public boolean deleteDocuments(String indexName, String type, Set<String> idsToDelete);

	/**
	 * checks if the client can connect to the es instance
	 * @return
	 */
	public boolean isValidConnection();
}
