package org.bibsonomy.search.es.update;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.SearchPost;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.index.ESResourceMapping;
import org.bibsonomy.search.es.management.ElasticSearchIndex;
import org.bibsonomy.search.es.management.util.ElasticSearchUtils;
import org.bibsonomy.search.update.SearchIndexState;
import org.bibsonomy.search.update.SearchIndexUpdater;
import org.bibsonomy.util.ValidationUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.index.query.QueryBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO: add documentation to this class
 *
 * @author dzo
 * @author jensi
 * @author lutful
 * @param <R> 
 */
public class ElasticSearchIndexUpdater<R extends Resource> implements SearchIndexUpdater<R> {
	private static final Log log = LogFactory.getLog(ElasticSearchIndexUpdater.class);
	
	/** the client to use for communication with elastic search cluster */
	protected ESClient esClient;
	
	/** the index to update */
	protected ElasticSearchIndex<R> index;
	
	/**
	 * @param esClient
	 * @param index
	 */
	public ElasticSearchIndexUpdater(ESClient esClient, ElasticSearchIndex<R> index) {
		super();
		this.esClient = esClient;
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.update.SearchIndexUpdater#createEmptyIndex()
	 */
	@Override
	public void createEmptyIndex() throws IOException {
		this.esClient.waitForReadyState();
		final String indexName = this.index.getIndexName();
		
		// check if the index already exists if not, it creates empty index
		final IndicesAdminClient indices = this.esClient.getClient().admin().indices();
		final boolean indexExist = indices.exists(new IndicesExistsRequest(indexName)).actionGet().isExists();
		if (!indexExist) {
			log.info("index not existing - generating a new one");
			final CreateIndexResponse createIndex = indices.create(new CreateIndexRequest(indexName)).actionGet();
			if (!createIndex.isAcknowledged()) {
				log.error("Error in creating Index");
				return;
			}
		}
		
		log.debug("Start writing mapping to shared index");
		
		// add mapping here depending on the resource type which is here indexType
		ESResourceMapping resourceMapping = new ESResourceMapping(this.index.getResourceType(), esClient, indexName);
		resourceMapping.doMapping();
		
		log.debug("wrote mapping to shared index");
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchIndexUpdater#deletePostWithContentId(java.util.List)
	 */
	@Override
	public void deletePostWithContentId(final int contentId) {
		final String indexID = ElasticSearchUtils.createElasticSearchId(contentId);
		this.deletePostForIndexId(indexID);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchIndexUpdater#insertPost(org.bibsonomy.search.SearchPost)
	 */
	@Override
	public void insertPost(SearchPost<R> post) {
		this.esClient.waitForReadyState();
		
		final Map<String, Object> jsonDocument = this.index.getContainer().getConverter().convert(post);
		// jsonDocument.put(Fields.SYSTEM_URL, this.systemHome); TODO: handle Systemhome TODODZO
		final String indexId = ElasticSearchUtils.createElasticSearchId(post.getContentId().intValue());
		// TODO: this method needs to support the additional parameter?
		this.insertPostDocument(jsonDocument, indexId);
	}
	
	private void insertPostDocument(final Map<String, Object> jsonDocument, String indexIdStr) {
		this.esClient.getClient().prepareIndex(this.index.getIndexName(), this.index.getResourceTypeAsString(), indexIdStr)
			.setSource(jsonDocument).setRefresh(true).execute().actionGet();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchIndexUpdater#removeAllPostsOfUser(java.lang.String)
	 */
	@Override
	public void removeAllPostsOfUser(final String userName) {
		// FIXME: system url TODODZO?
		this.esClient.getClient().prepareDeleteByQuery(this.index.getIndexName())
			.setTypes(this.index.getResourceTypeAsString())
			.setQuery(QueryBuilders.termQuery(Fields.USER_NAME, userName))
			.execute().actionGet();
	}
	
	private void deletePostForIndexId(final String indexId) {
		// FIXME: system url TODODZO?
		this.esClient.getClient().prepareDelete(this.index.getIndexName(), this.index.getResourceTypeAsString(), indexId)
		.setRefresh(true)
		.execute().actionGet();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.update.SearchIndexUpdater#updateSystemInformation(org.bibsonomy.search.update.IndexUpdaterState)
	 */
	@Override
	public void updateIndexState(SearchIndexState newState) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonDocumentForSystemInfo = mapper.writeValueAsString(newState);
			final String indexName = this.index.getIndexName();
			final IndexResponse res = this.esClient.getClient().prepareIndex(indexName, ESConstants.SYSTEM_INFO_INDEX_TYPE, ESConstants.SYSTEM_INFO_INDEX_TYPE).setSource(jsonDocumentForSystemInfo).execute().actionGet();
			if ((res == null) || !ValidationUtils.present(res.getId())) {
				throw new RuntimeException("failed to save systeminformation for index " + indexName);
			}
			
			log.info("updated systeminformation of index " + indexName + " to " + jsonDocumentForSystemInfo);
		} catch (final JsonProcessingException e) {
			log.error("Failed to convert " + newState.getClass().getSimpleName() + " into JSON", e);
		}
	}
}
