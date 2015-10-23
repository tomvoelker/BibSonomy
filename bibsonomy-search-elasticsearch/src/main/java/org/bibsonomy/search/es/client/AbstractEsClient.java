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
package org.bibsonomy.search.es.client;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.management.util.ElasticSearchUtils;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.search.util.Mapping;
import org.bibsonomy.util.ValidationUtils;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public abstract class AbstractEsClient implements ESClient {
	private static final Log log = LogFactory.getLog(AbstractEsClient.class);

	/**
	 * waits for the yellow (or green) status to prevent NoShardAvailableActionException later
	 */
	@Override
	public void waitForReadyState() {
		this.getClient().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
	}

	@Override
	public boolean createIndex(String indexName, Set<Mapping<String>> mappings) {
		final CreateIndexResponse createIndex = this.getClient().admin().indices().create(new CreateIndexRequest(indexName)).actionGet();
		if (!createIndex.isAcknowledged()) {
			log.error("Error in creating Index");
			return false;
		}
		
		for (final Mapping<String> mapping : mappings) {
			this.getClient().admin().indices().preparePutMapping(indexName).setType(mapping.getType()).setSource(mapping.getMappingInfo()).execute().actionGet();
		}
		
		// wait for the yellow (or green) status to prevent
		// NoShardAvailableActionException later
		this.waitForReadyState();
		return true;
	}

	@Override
	public SearchIndexSyncState getSearchIndexStateForIndex(String indexName) {
		// wait for the yellow (or green) status to prevent
		// NoShardAvailableActionException later
		this.waitForReadyState();
		
		final SearchRequestBuilder searchRequestBuilder = this.getClient().prepareSearch(indexName);
		searchRequestBuilder.setTypes(ESConstants.SYSTEM_INFO_INDEX_TYPE);
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setFrom(0).setSize(1).setExplain(true); // FIXME: remove explain
		
		final SearchResponse searchResponse = searchRequestBuilder.get();
		
		// ensure that there is only one index state stored in the index
		long hitsInIndex = searchResponse.getHits().totalHits();
		if (hitsInIndex > 1) {
			throw new IllegalStateException(hitsInIndex + " systeminfos for index " + indexName);
		}
		
		return ElasticSearchUtils.deserializeSearchIndexState(searchResponse.getHits().iterator().next().getSource());
	}

	@Override
	public boolean insertNewDocument(String indexName, String type, String id, Map<String, Object> jsonDocument) {
		final IndexResponse indexResponse = this.getClient().prepareIndex(indexName, type, id).setSource(jsonDocument).setRefresh(true).get();
		return ((indexResponse != null) && ValidationUtils.present(indexResponse.getId()));
	}

	@Override
	public boolean removeDocumentFromIndex(String indexName, String type, String indexID) {
		final DeleteResponse deleteResponse = this.getClient().delete(new DeleteRequest(indexName, type, indexID)).actionGet();
		return deleteResponse.getId() != null;
	}

	@Override
	public boolean deleteIndex(String oldIndexName) {
		final DeleteIndexResponse deleteResult = this.getClient().admin().indices().delete(new DeleteIndexRequest(oldIndexName)).actionGet();
		return deleteResult.isAcknowledged();
	}

	@Override
	public boolean existsIndexWithName(String indexName) {
		final IndicesAdminClient indices = this.getClient().admin().indices();
		return indices.exists(new IndicesExistsRequest(indexName)).actionGet().isExists();
	}

	@Override
	public boolean createAlias(String indexName, String alias) {
		final IndicesAliasesRequestBuilder prepareAliases = this.getClient().admin().indices().prepareAliases();
		
		prepareAliases.addAlias(indexName, alias);
		final IndicesAliasesResponse aliasReponse = prepareAliases.execute().actionGet();
		if (!aliasReponse.isAcknowledged()) {
			log.error("error creating alias '" + alias + "' for index '" + indexName + "'.");
			return false;
		}
		return false;
	}

	@Override
	public boolean updateAliases(Set<Pair<String, String>> aliasesToAdd, Set<Pair<String, String>> aliasesToRemove) {
		final IndicesAliasesRequestBuilder aliasesBuilder = this.getClient().admin().indices().prepareAliases();
		
		if (present(aliasesToAdd)) {
			for (Pair<String, String> aliasToAdd : aliasesToAdd) {
				aliasesBuilder.addAlias(aliasToAdd.getFirst(), aliasToAdd.getSecond());
			}
		}
		
		if (present(aliasesToRemove)) {
			for (Pair<String, String> aliasToRemove : aliasesToRemove) {
				aliasesBuilder.removeAlias(aliasToRemove.getFirst(), aliasToRemove.getSecond());
			}
		}
		
		final IndicesAliasesResponse aliasResponse = aliasesBuilder.get();
		return aliasResponse.isAcknowledged();
	}

	@Override
	public void deleteAlias(String indexName, String alias) {
		this.updateAliases(null, Collections.singleton(new Pair<>(indexName, alias)));
	}

	@Override
	public String getIndexNameForAlias(final String alias) {
		final ImmutableOpenMap<String, List<AliasMetaData>> activeindices = this.getClient().admin().indices().getAliases(new GetAliasesRequest().aliases(alias)).actionGet().getAliases();
		if (!activeindices.isEmpty()) {
			if (activeindices.size() > 1) {
				throw new IllegalStateException("found more than one index for this system!");
			}
			
			return activeindices.iterator().next().key;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.ESClient#getDocumentCount(java.lang.String, org.elasticsearch.index.query.QueryBuilder)
	 */
	@Override
	public long getDocumentCount(String indexName, String type, QueryBuilder query) {
		if (query == null) {
			final IndicesStatsResponse statResponse = this.getClient().admin().indices().prepareStats(indexName).setTypes(type).setStore(true).execute().actionGet();
			return statResponse.getTotal().getDocs().getCount() - 1;
		}
		final CountRequestBuilder count = this.getClient().prepareCount(indexName);
		count.setTypes(type);
		final CountResponse response = count.setQuery(query).get();
		return response.getCount();
	}

	@Override
	public void shutdown() {
		this.getClient().close();
	}
}
