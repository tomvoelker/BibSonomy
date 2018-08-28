package org.bibsonomy.search.es.client;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.bibsonomy.search.util.Mapping;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * implementation of {@link ESClient} using the REST client of Elasticsearch
 * @author dzo
 */
public class ElasticsearchRESTClient implements ESClient {
	private static final Log LOG = LogFactory.getLog(ElasticsearchRESTClient.class);

	@FunctionalInterface
	private interface RESTCall<T> {
		T call() throws IOException;
	}

	private RestHighLevelClient client;

	/**
	 * @param client the rest client to use
	 */
	public ElasticsearchRESTClient(RestHighLevelClient client) {
		this.client = client;
	}

	@Override
	public void waitForReadyState() {
		secureCall(() -> {
			final ClusterHealthRequest healthRequest = new ClusterHealthRequest();
			healthRequest.waitForYellowStatus();
			this.client.cluster().health(healthRequest, this.buildRequestOptions());
			return null;
		} , null, "error while calling health api");
	}

	private RequestOptions buildRequestOptions() {
		return RequestOptions.DEFAULT;
	}

	private <R> R secureCall(final RESTCall<R> call, R defaultValue, String message) {
		try {
			return call.call();
		} catch (IOException e) {
			LOG.error(message, e);
		}

		return defaultValue;
	}

	@Override
	public List<String> getIndexNamesForAlias(final String alias) {
		return this.secureCall(() -> {
			final GetAliasesRequest getAliasesRequest = new GetAliasesRequest();
			getAliasesRequest.aliases(alias);
			final GetAliasesResponse response = this.client.indices().getAlias(getAliasesRequest, this.buildRequestOptions());
			final Map<String, Set<AliasMetaData>> aliases = response.getAliases();

			final Stream<String> indexNames = aliases.get(alias).stream().map((metaData) -> metaData.indexRouting());
			return indexNames.collect(Collectors.toList());
		}, new LinkedList<>(), "error getting index names for alias " + alias);
	}

	@Override
	public boolean insertNewDocument(String indexName, String type, String id, Map<String, Object> jsonDocument) {
		return this.secureCall(() -> {
			final IndexRequest indexRequest = buildIndexRequest(indexName, type, id, jsonDocument);
			final IndexResponse response = this.client.index(indexRequest, this.buildRequestOptions());
			return response.getResult() == DocWriteResponse.Result.CREATED;
		}, false, "error while inserting new document");
	}

	private static IndexRequest buildIndexRequest(String indexName, String type, String id, Map<String, Object> jsonDocument) {
		final IndexRequest indexRequest = new IndexRequest();
		indexRequest.index(indexName);
		indexRequest.type(type); // TODO: remove with es 7
		indexRequest.id(id);
		indexRequest.source(jsonDocument);
		return indexRequest;
	}

	@Override
	public boolean insertNewDocuments(String indexName, String type, Map<String, Map<String, Object>> jsonDocuments) {
		return this.secureCall(() -> {
			final BulkRequest bulkRequest = new BulkRequest();
			// convert each document to a indexrequest object and add all to the request
			final Stream<IndexRequest> indexRequests = jsonDocuments.entrySet().stream().map(entity -> buildIndexRequest(indexName, type, entity.getKey(), entity.getValue()));
			indexRequests.forEach(bulkRequest::add);

			final BulkResponse bulkResponse = this.client.bulk(bulkRequest, this.buildRequestOptions());
			return !bulkResponse.hasFailures();
		}, false, "error while inserting new documents into index " + indexName);
	}

	@Override
	public boolean existsIndexWithName(String indexName) {
		return this.secureCall(() -> {
			final GetIndexRequest request = new GetIndexRequest();
			request.indices(indexName);
			return this.client.indices().exists(request, this.buildRequestOptions());
		}, false, "error while checking for existing index " + indexName);
	}

	@Override
	public SearchIndexSyncState getSearchIndexStateForIndex(String indexName) {
		return null;
	}

	@Override
	public boolean createIndex(String indexName, Mapping<String> mapping, String settings) {
		return secureCall(() -> {
			final CreateIndexRequest createIndexRequest = new CreateIndexRequest();
			createIndexRequest.index(indexName);
			createIndexRequest.mapping(mapping.getType(), mapping.getMappingInfo()); // FIXME: not working!
			createIndexRequest.settings(settings, XContentType.JSON); // FIXME: not working
			final CreateIndexResponse response = this.client.indices().create(createIndexRequest, this.buildRequestOptions());
			return response.isAcknowledged();
		}, false, "error creating index " + indexName);
	}

	@Override
	public boolean deleteIndex(final String indexName) {
		return secureCall(() -> {
			final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest();
			deleteIndexRequest.indices(indexName);

			final DeleteIndexResponse response = this.client.indices().delete(deleteIndexRequest, this.buildRequestOptions());
			return response.isAcknowledged();
		}, false, "error deleting index " + indexName);
	}

	@Override
	public boolean updateAliases(Set<Pair<String, String>> aliasesToAdd, Set<Pair<String, String>> aliasesToRemove) {
		return this.secureCall(() -> {
			final IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();

			// convert each pair of aliasesToAdd to a aliasAction
			final Stream<IndicesAliasesRequest.AliasActions> aliasesToAddStream = aliasesToAdd.stream().map(aliasInfo -> createAliasAction(IndicesAliasesRequest.AliasActions.Type.ADD, aliasInfo.getFirst(), aliasInfo.getSecond()));
			aliasesToAddStream.forEach(indicesAliasesRequest::addAliasAction);

			// do the same for the aliasesToRemove
			final Stream<IndicesAliasesRequest.AliasActions> aliasesToRemoveStream = aliasesToRemove.stream().map(aliasInfo -> createAliasAction(IndicesAliasesRequest.AliasActions.Type.REMOVE, aliasInfo.getFirst(), aliasInfo.getSecond()));
			aliasesToRemoveStream.forEach(indicesAliasesRequest::addAliasAction);

			// call elasticsearch
			final IndicesAliasesResponse response = this.client.indices().updateAliases(indicesAliasesRequest, this.buildRequestOptions());
			return response.isAcknowledged();
		}, false, "error updating aliases");
	}

	private static IndicesAliasesRequest.AliasActions createAliasAction(IndicesAliasesRequest.AliasActions.Type type, String index, String alias) {
		final IndicesAliasesRequest.AliasActions aliasAction = new IndicesAliasesRequest.AliasActions(type);
		aliasAction.index(index).alias(alias);
		return aliasAction;
	}

	@Override
	public long getDocumentCount(String indexName, String type, QueryBuilder query) {
		final QueryBuilder countQuery = query == null ? QueryBuilders.matchAllQuery() : query;
		final Number count = this.secureCall(() -> {
			final SearchRequest searchRequest = new SearchRequest();
			searchRequest.indices(indexName);
			searchRequest.types(type);
			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(countQuery);
			searchSourceBuilder.size(0); // we are counting the posts
			searchRequest.source(searchSourceBuilder);
			final SearchResponse response = this.client.search(searchRequest, this.buildRequestOptions());
			return response.getHits().getTotalHits();
		}, 0, "error getting document count");
		return count.longValue();
	}

	@Override
	public boolean updateDocument(String indexName, String type, String id, Map<String, Object> jsonDocument) {
		return this.secureCall(() -> {
			final UpdateRequest updateRequest = new UpdateRequest();
			updateRequest.id(id);
			updateRequest.type(type);
			updateRequest.index(indexName);
			updateRequest.doc(jsonDocument);
			final UpdateResponse updateResponse = this.client.update(updateRequest, this.buildRequestOptions());
			return updateResponse.getResult() == DocWriteResponse.Result.UPDATED;
		}, false, "error while updating document " + id);
	}

	@Override
	public SearchHits search(String indexName, String type, QueryBuilder queryBuilder, HighlightBuilder highlightBuilder, Pair<String, SortOrder> order, int offset, int limit, Float minScore, Set<String> fieldsToRetrieve) {
		return this.secureCall(() -> {
			final SearchRequest searchRequest = new SearchRequest();
			searchRequest.searchType(SearchType.DEFAULT);
			searchRequest.types(type);
			searchRequest.indices(indexName);

			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(queryBuilder);
			searchSourceBuilder.size(limit);
			searchSourceBuilder.from(offset);
			searchSourceBuilder.highlighter(highlightBuilder);

			if (present(minScore)) {
				searchSourceBuilder.minScore(minScore.floatValue());
			}

			if (present(fieldsToRetrieve)) {
				// TODO: support multiple fields
				searchSourceBuilder.fetchSource(fieldsToRetrieve.iterator().next(), null);
			}

			if (present(order)) {
				searchSourceBuilder.sort(order.getFirst(), order.getSecond());
			}
			searchRequest.source(searchSourceBuilder);
			final SearchResponse search = this.client.search(searchRequest, this.buildRequestOptions());
			return search.getHits();
		}, null, "error while searching");
	}

	@Override
	public void deleteDocuments(String indexName, String type, QueryBuilder query) {
		this.secureCall(() -> {
			final SearchRequest searchRequest = new SearchRequest(indexName);
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(query);
			searchSourceBuilder.size(200);
			searchRequest.source(searchSourceBuilder);
			searchRequest.scroll(TimeValue.timeValueMinutes(3L));
			final SearchResponse searchResponse = this.client.search(searchRequest, RequestOptions.DEFAULT);
			final String scrollId = searchResponse.getScrollId();

			final SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);

			while (true) {
				final SearchResponse scrollResponse = this.client.scroll(searchScrollRequest, this.buildRequestOptions());
				final BulkRequest bulkRequest = new BulkRequest();
				final SearchHit[] hits = scrollResponse.getHits().getHits();
				if (hits.length == 0) {
					break;
				}
				final Stream<DeleteRequest> deleteRequestsStream = Arrays.stream(hits).map(hit -> new DeleteRequest(indexName).type(type).id(hit.getId()));
				deleteRequestsStream.forEach(bulkRequest::add);

				this.client.bulk(bulkRequest, this.buildRequestOptions());
			}

			final ClearScrollRequest request = new ClearScrollRequest();
			request.addScrollId(scrollId);
			this.client.clearScroll(request, this.buildRequestOptions());

			return null;
		}, null, "error deleting documents form index " + indexName);
	}

	@Override
	public boolean deleteDocuments(String indexName, String type, Set<String> idsToDelete) {
		return this.secureCall(() -> {
			final BulkRequest bulkRequest = new BulkRequest();

			final Stream<DeleteRequest> deleteRequestsStream = idsToDelete.stream().map(id -> new DeleteRequest().id(id).type(type).index(indexName));
			deleteRequestsStream.forEach(bulkRequest::add);

			final BulkResponse bulkResponse = this.client.bulk(bulkRequest, this.buildRequestOptions());
			return !bulkResponse.hasFailures();
		}, false, "error deleting documents from index");
	}

	@Override
	public void shutdown() {
		try {
			this.client.close();
		} catch (IOException e) {
			LOG.error("error while closing client");
		}
	}
}
