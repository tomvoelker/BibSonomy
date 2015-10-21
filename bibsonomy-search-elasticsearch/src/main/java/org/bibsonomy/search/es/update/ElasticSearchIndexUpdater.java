package org.bibsonomy.search.es.update;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.SearchPost;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.management.ElasticSearchIndex;
import org.bibsonomy.search.es.management.util.ElasticSearchUtils;
import org.bibsonomy.search.management.IndexLock;
import org.bibsonomy.search.management.SearchIndexContainer;
import org.bibsonomy.search.update.SearchIndexState;
import org.bibsonomy.search.update.SearchIndexUpdater;
import org.bibsonomy.search.util.Mapping;
import org.bibsonomy.util.ValidationUtils;
import org.elasticsearch.action.index.IndexResponse;
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
	protected IndexLock<R, Map<String, Object>, ElasticSearchIndex<R>, String> indexLock;
	
	/**
	 * @param esClient
	 * @param indexLock
	 */
	public ElasticSearchIndexUpdater(ESClient esClient, IndexLock<R, Map<String, Object>, ElasticSearchIndex<R>, String> indexLock) {
		super();
		this.esClient = esClient;
		this.indexLock = indexLock;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.update.SearchIndexUpdater#createEmptyIndex()
	 */
	@Override
	public void createEmptyIndex() throws IOException {
		this.esClient.waitForReadyState();
		final ElasticSearchIndex<R> searchIndex = getIndex();
		final String indexName = searchIndex.getIndexName();
		
		// check if the index already exists if not, it creates empty index
		final boolean indexExists = this.esClient.existsIndexWithName(indexName);
		if (indexExists) {
			throw new IllegalStateException("index '" + indexName + "' already exists while generating an index");
		}
		
		final SearchIndexContainer<R, Map<String, Object>, ElasticSearchIndex<R>, String> container = searchIndex.getContainer();
		final Mapping<String> mapping = container.getMappingBuilder().getMapping();
		log.info("index not existing - generating a new one with mapping");
		
		final boolean created = this.esClient.createIndex(indexName, Collections.singleton(mapping));
		if (!created) {
			throw new RuntimeException("can not create index '" + indexName + "'"); // TODO: use specific exception
		}
		
		// FIXME: use system url TODODZO
		this.esClient.createAlias(indexName, ElasticSearchUtils.getTempAliasForResource(container.getResourceType()));
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
		
		final Map<String, Object> jsonDocument = this.indexLock.getSearchIndex().getContainer().getConverter().convert(post);
		// jsonDocument.put(Fields.SYSTEM_URL, this.systemHome); TODO: handle Systemhome TODODZO
		final String indexId = ElasticSearchUtils.createElasticSearchId(post.getContentId().intValue());
		// TODO: this method needs to support the additional parameter?
		this.insertPostDocument(jsonDocument, indexId);
	}
	
	private void insertPostDocument(final Map<String, Object> jsonDocument, String indexIdStr) {
		final ElasticSearchIndex<R> searchIndex = getIndex();
		this.esClient.insertNewDocument(searchIndex.getIndexName(), searchIndex.getContainer().getResourceTypeAsString(), indexIdStr, jsonDocument);
	}
	
	/**
	 * @return the index hold by the lock
	 */
	protected ElasticSearchIndex<R> getIndex() {
		return this.indexLock.getSearchIndex();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchIndexUpdater#removeAllPostsOfUser(java.lang.String)
	 */
	@Override
	public void removeAllPostsOfUser(final String userName) {
		final ElasticSearchIndex<R> index = this.getIndex();
		// FIXME: system url TODODZO?
		this.esClient.getClient().prepareDeleteByQuery(index.getIndexName())
			.setTypes(index.getContainer().getResourceTypeAsString())
			.setQuery(QueryBuilders.termQuery(Fields.USER_NAME, userName))
			.execute().actionGet();
	}
	
	private void deletePostForIndexId(final String indexId) {
		final ElasticSearchIndex<R> index = this.getIndex();
		// FIXME: system url TODODZO?
		this.esClient.getClient().prepareDelete(index.getIndexName(), index.getContainer().getResourceTypeAsString(), indexId)
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
			final String indexName = this.getIndex().getIndexName();
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
