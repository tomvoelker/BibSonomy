package org.bibsonomy.search.es.client;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
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
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
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
import java.util.stream.Stream;

/**
 * implementation of {@link ESClient} using the REST client of Elasticsearch
 *
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
		}, null, "error while calling health api");
	}

	private RequestOptions buildRequestOptions() {
		return RequestOptions.DEFAULT;
	}

	private <R> R secureCall(final RESTCall<R> call, R defaultValue, String message) {
		try {
			return call.call();
		} catch (final IOException e) {
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
			return new LinkedList<>(aliases.keySet());
		}, new LinkedList<>(), "error getting index names for alias " + alias);
	}

	@Override
	public boolean insertNewDocument(String indexName, String id, IndexData indexData) {
		return this.secureCall(() -> {
			final IndexRequest indexRequest = buildIndexRequest(indexName, id, indexData);
			final IndexResponse response = this.client.index(indexRequest, this.buildRequestOptions());
			return response.getResult() == DocWriteResponse.Result.CREATED;
		}, false, "error while inserting new document");
	}

	private static IndexRequest buildIndexRequest(String indexName, String id, IndexData indexData) {
		final IndexRequest indexRequest = new IndexRequest();
		return indexRequest.index(indexName)
								.routing(indexData.getRouting())
								.type(indexData.getType()) // TODO: remove with es 7
								.id(id)
								.source(indexData.getSource());
	}

	@Override
	public boolean insertNewDocuments(String indexName, Map<String, IndexData> jsonDocuments) {
		return this.secureCall(() -> {
			final BulkRequest bulkRequest = new BulkRequest();
			// convert each document to a indexrequest object and add all to the request
			final Stream<IndexRequest> indexRequests = jsonDocuments.entrySet().stream().map(entity -> buildIndexRequest(indexName,entity.getKey(), entity.getValue()));

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
	public DefaultSearchIndexSyncState getSearchIndexStateForIndex(String indexName, String syncStateForIndexName) {
		return this.secureCall(() -> {
			final GetRequest getRequest = new GetRequest();
			getRequest.id(syncStateForIndexName);
			getRequest.index(indexName);
			final GetResponse response = this.client.get(getRequest, this.buildRequestOptions());
			if (!response.isExists()) {
				throw new IllegalStateException("no index sync state found for " + indexName);
			}
			return ElasticsearchUtils.deserializeSearchIndexState(response.getSourceAsMap());
		}, null, "error getting search index sync state for index " + syncStateForIndexName);
	}

	@Override
	public boolean createIndex(String indexName, Mapping<XContentBuilder> mapping, String settings) {
		return secureCall(() -> {
			final CreateIndexRequest createIndexRequest = new CreateIndexRequest();
			createIndexRequest.index(indexName);
			createIndexRequest.mapping(mapping.getType(), mapping.getMappingInfo());
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
	public void deleteDocuments(final String indexName, final String type, final QueryBuilder query) {
		this.secureCall(() -> {
			final SearchRequest searchRequest = new SearchRequest(indexName);
			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(query);
			searchSourceBuilder.size(200);
			searchRequest.types(type);
			searchRequest.source(searchSourceBuilder);
			searchRequest.scroll(TimeValue.timeValueMinutes(3L));

			// create the scroll search and get the first results
			final SearchResponse searchResponse = this.client.search(searchRequest, this.buildRequestOptions());
			final String scrollId = searchResponse.getScrollId();
			final SearchHits firstHits = searchResponse.getHits();
			if (firstHits.getTotalHits() > 0) {
				this.bulkDeleteHits(indexName, type, firstHits.getHits());
			}

			final SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
			searchScrollRequest.scroll(TimeValue.timeValueMinutes(3L));

			while (true) {
				final SearchResponse scrollResponse = this.client.scroll(searchScrollRequest, this.buildRequestOptions());

				final SearchHit[] hits = scrollResponse.getHits().getHits();
				if (hits.length == 0) {
					break;
				}
				this.bulkDeleteHits(indexName, type, hits);
			}

			final ClearScrollRequest request = new ClearScrollRequest();
			request.addScrollId(scrollId);
			this.client.clearScroll(request, this.buildRequestOptions());
			return null;
		}, null, "error deleting documents form index " + indexName);
	}

	private void bulkDeleteHits(String indexName, String type, SearchHit[] hits) throws IOException {
		final BulkRequest bulkRequest = new BulkRequest();
		bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
		final Stream<DeleteRequest> deleteRequestsStream = Arrays.stream(hits).map(hit -> new DeleteRequest(indexName).type(type).id(hit.getId()));

		deleteRequestsStream.forEach(bulkRequest::add);
		final BulkResponse bulkResponse = this.client.bulk(bulkRequest, this.buildRequestOptions());

		if (bulkResponse.hasFailures()) {
			LOG.error(bulkResponse.buildFailureMessage());
		}
	}

	@Override
	public boolean deleteDocuments(String indexName, List<DeleteData> documentsToDelete) {
		if (!present(documentsToDelete)) {
			// nothing to delete
			return true;
		}

		return this.secureCall(() -> {
			final BulkRequest bulkRequest = new BulkRequest();

			final Stream<DeleteRequest> deleteRequestsStream = documentsToDelete.stream().map(deleteData -> new DeleteRequest().id(deleteData.getId()).type(deleteData.getType()).routing(deleteData.getRouting()).index(indexName));
			deleteRequestsStream.forEach(bulkRequest::add);

			final BulkResponse bulkResponse = this.client.bulk(bulkRequest, this.buildRequestOptions());
			return !bulkResponse.hasFailures();
		}, false, "error deleting documents from index");
	}

	@Override
	public boolean isValidConnection() {
		try {
			return this.client.ping(this.buildRequestOptions());
		} catch (final Exception e) {
			LOG.error("disabling index", e);
		}

		return false;
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
