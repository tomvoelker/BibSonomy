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
package org.bibsonomy.search.es.client;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
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
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;

/**
 * the default implementation of the {@link ESClient}
 *
 * @author jensi
 */
public class ElasticsearchClient implements ESClient {
	
	private static final Log log = LogFactory.getLog(ElasticsearchClient.class);
	
	private final Client client;
	
	/**
	 * @param client
	 */
	public ElasticsearchClient(Client client) {
		super();
		this.client = client;
	}

	/**
	 * waits for the yellow (or green) status to prevent NoShardAvailableActionException later
	 */
	@Override
	public void waitForReadyState() {
		this.client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
	}

	@Override
	public boolean createIndex(final String indexName, final Mapping<String> mapping, final String settings) {
		final Settings.Builder settingsBuilder = Settings.builder();
		final CreateIndexResponse createIndex = this.client.admin().indices().create(new CreateIndexRequest(indexName, settingsBuilder.loadFromSource(settings, XContentType.JSON).build())).actionGet();
		if (!createIndex.isAcknowledged()) {
			log.error("Error in creating Index");
			return false;
		}
		this.client.admin().indices().preparePutMapping(indexName).setType(mapping.getType()).setSource(mapping.getMappingInfo()).execute().actionGet();
		
		// wait for the yellow (or green) status to prevent NoShardAvailableActionException later
		this.waitForReadyState();
		return true;
	}

	@Override
	public SearchIndexSyncState getSearchIndexStateForIndex(String indexName) {
		// wait for the yellow (or green) status to prevent NoShardAvailableActionException later
		this.waitForReadyState();
		
		final SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(indexName);
		searchRequestBuilder.setTypes(ESConstants.SYSTEM_INFO_INDEX_TYPE);
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setFrom(0).setSize(1).setExplain(true); // FIXME: remove explain
		
		final SearchResponse searchResponse = searchRequestBuilder.get();
		
		// ensure that there is only one index state stored in the index
		long hitsInIndex = searchResponse.getHits().getTotalHits();
		if (hitsInIndex != 1) {
			throw new IllegalStateException(hitsInIndex + " systeminfos for index " + indexName);
		}
		
		return ElasticsearchUtils.deserializeSearchIndexState(searchResponse.getHits().iterator().next().getSourceAsMap());
	}

	@Override
	public boolean insertNewDocument(String indexName, String type, String id, Map<String, Object> jsonDocument) {
		final IndexResponse indexResponse = this.client.prepareIndex(indexName, type, id).setSource(jsonDocument).setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL).get();
		return ((indexResponse != null) && ValidationUtils.present(indexResponse.getId()));
	}
	
	@Override
	public boolean insertNewDocuments(String indexName, String type, Map<String, Map<String, Object>> jsonDocuments) {
		if (jsonDocuments.isEmpty()) {
			log.info("no documents present; nothing to insert");
			return true;
		}

		final BulkRequestBuilder bulk = this.client.prepareBulk();
		
		for (Entry<String, Map<String, Object>> entryDocument : jsonDocuments.entrySet()) {
			bulk.add(this.client.prepareIndex(indexName, type, entryDocument.getKey()).setSource(entryDocument.getValue()));
		}
		
		final BulkResponse bulkResponse = bulk.get();
		final boolean hasFailures = bulkResponse.hasFailures();
		if (hasFailures) {
			log.error("failure while bulk insert " + bulkResponse.buildFailureMessage());
		}
		return !hasFailures;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.ESClient#prepareUpdate(java.lang.String, java.lang.String, java.lang.String)
	 */
	//@Override
	public UpdateRequestBuilder prepareUpdate(String indexName, String type, String id) {
		return this.client.prepareUpdate(indexName, type, id);
	}

	@Override
	public boolean deleteIndex(String indexName) {
		final DeleteIndexResponse deleteResult = this.client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
		return deleteResult.isAcknowledged();
	}

	@Override
	public boolean existsIndexWithName(String indexName) {
		final IndicesAdminClient indices = this.client.admin().indices();
		return indices.exists(new IndicesExistsRequest(indexName)).actionGet().isExists();
	}

	@Override
	public boolean createAlias(String indexName, String alias) {
		final IndicesAliasesRequestBuilder prepareAliases = this.client.admin().indices().prepareAliases();
		
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
		final IndicesAliasesRequestBuilder aliasesBuilder = this.client.admin().indices().prepareAliases();
		
		if (present(aliasesToAdd)) {
			for (final Pair<String, String> aliasToAdd : aliasesToAdd) {
				aliasesBuilder.addAlias(aliasToAdd.getFirst(), aliasToAdd.getSecond());
			}
		}
		
		if (present(aliasesToRemove)) {
			for (final Pair<String, String> aliasToRemove : aliasesToRemove) {
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
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.ESClient#getIndexNamesForAlias(java.lang.String)
	 */
	@Override
	public List<String> getIndexNamesForAlias(String aliasName) {
		final ImmutableOpenMap<String, List<AliasMetaData>> activeindices = this.client.admin().indices().getAliases(new GetAliasesRequest().aliases(aliasName)).actionGet().getAliases();
		final List<String> indexNames = new LinkedList<>();

		for (final ObjectObjectCursor<String, List<AliasMetaData>> objectObjectCursor : activeindices) {
			indexNames.add(objectObjectCursor.key);
		}
		return indexNames;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.ESClient#getDocumentCount(java.lang.String, org.elasticsearch.index.query.QueryBuilder)
	 */
	@Override
	public long getDocumentCount(String indexName, String type, QueryBuilder query) {
		if (query == null) {
			query = QueryBuilders.matchAllQuery();
		}
		final SearchRequestBuilder count = this.client.prepareSearch(indexName);
		count.setTypes(type);
		count.setSize(0);
		final SearchResponse response = count.setQuery(query).get();
		return response.getHits().getTotalHits();
	}

	@Override
	public SearchHits search(String indexName, String type, QueryBuilder queryBuilder, HighlightBuilder highlightBuilder, Pair<String, SortOrder> order, int offset, int limit, Float minScore, Set<String> fieldsToRetrieve) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.ESClient#deleteDocuments(java.lang.String, java.lang.String, org.elasticsearch.index.query.TermQueryBuilder)
	 */
	@Override
	public void deleteDocuments(final String indexName, final String type, final QueryBuilder query) {
		final SearchRequestBuilder searchRequest = this.client.prepareSearch(indexName);
		searchRequest.setTypes(type);
		searchRequest.setQuery(query);
		searchRequest.setScroll(new TimeValue(3, TimeUnit.MINUTES));
		searchRequest.setSize(200); // note: per shard!
		
		final SearchResponse searchResponse = searchRequest.get();
		final String scrollId = searchResponse.getScrollId();
		
		SearchResponse scrollResponse;
		
		while (true) {
			scrollResponse = this.client.prepareSearchScroll(scrollId).get();
			final SearchHit[] hits = scrollResponse.getHits().getHits();
			if (hits.length == 0) {
				break;
			}
			
			final BulkRequestBuilder bulkRequest = this.client.prepareBulk();
			
			for (final SearchHit hit : hits) {
				bulkRequest.add(new DeleteRequest(indexName, type, hit.getId()));
			}
			
			bulkRequest.get();
		}
		
		// close the scroll
		this.client.prepareClearScroll().setScrollIds(Collections.singletonList(scrollId)).get();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.ESClient#deleteDocuments(java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public boolean deleteDocuments(final String indexName, final String type, Set<String> idsToDelete) {
		if (!present(idsToDelete)) {
			return true;
		}
		final BulkRequestBuilder bulkRequest = this.client.prepareBulk();
		for (final String id : idsToDelete) {
			bulkRequest.add(new DeleteRequest(indexName, type, id));
		}
		
		final BulkResponse bulkResponse = bulkRequest.get();
		return !bulkResponse.hasFailures();
	}

	@Override
	public boolean updateDocument(String indexName, String type, String id, Map<String, Object> jsonDocument) {
		return false;
	}

	/**
	 * @return <code>true</code> if a connection to es can be established 
	 */
	public boolean isValidConnection() {
		try {
			this.waitForReadyState();
			return true;
		} catch (final Exception e) {
			log.error("disabled indexing", e);
			return false;
		}
	}

	@Override
	public void shutdown() {
		this.client.close();
	}
}
