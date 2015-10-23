/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import java.util.Map;
import java.util.Set;

import org.bibsonomy.common.Pair;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.search.util.Mapping;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * Wrapper around an ElasticSearch Client. Different ways of obtaining a Client
 * can be implemented in classes implementing this interface.
 * 
 * @author lka
 */
public interface ESClient {
	/**
	 * Get a reference to an ElasticSearch Client.
	 * 
	 * @return the Client.
	 */
	@Deprecated
	Client getClient();

	/**
	 * if necessary wait for the index to be ready to work
	 */
	public void waitForReadyState();

	/**
	 * @param indexName
	 * @param alias
	 * @return <code>true</code> iff the index was updated
	 */
	boolean createAlias(String indexName, String alias);

	/**
	 * @param alias
	 * @return the index name of the alias
	 * @throws IllegalStateException if an alias has multiple indices
	 */
	public String getIndexNameForAlias(String alias);

	/**
	 * @param indexName
	 * @param type
	 * @param id
	 * @param jsonDocument
	 * @return 
	 */
	boolean insertNewDocument(String indexName, String type, String id, Map<String, Object> jsonDocument);
	
	/**
	 * @param indexName
	 * @return <code>true</code> if the index exists on the cluster
	 */
	boolean existsIndexWithName(String indexName);
	
	/**
	 * @param indexName
	 * @return
	 */
	public SearchIndexSyncState getSearchIndexStateForIndex(String indexName);
	
	/**
	 * @param indexName
	 * @param mappings 
	 * @return
	 */
	boolean createIndex(String indexName, Set<Mapping<String>> mappings);
	
	/**
	 * @param oldIndexName
	 * @return 
	 */
	boolean deleteIndex(String oldIndexName);
	
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

	/**
	 * @param indexName
	 * @param resourceTypeAsString
	 * @param indexID
	 * @return 
	 */
	boolean removeDocumentFromIndex(String indexName, String resourceTypeAsString, String indexID);

	/**
	 * @param indexName
	 * @param alias
	 */
	void deleteAlias(String indexName, String alias);

	/**
	 * @param indexName
	 * @param query
	 * @return
	 */
	long getDocumentCount(String indexName, QueryBuilder query);
}
