package org.bibsonomy.search.es.management;

import java.net.URI;
import java.util.Map;

import org.bibsonomy.model.Resource;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.generator.ElasticSearchIndexGeneratorTask;
import org.bibsonomy.search.es.management.util.ElasticSearchUtils;
import org.bibsonomy.search.es.update.ElasticSearchIndexUpdater;
import org.bibsonomy.search.generator.SearchIndexGeneratorTask;
import org.bibsonomy.search.management.SearchIndexContainer;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.SearchIndexUpdater;
import org.bibsonomy.search.util.ResourceConverter;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.client.IndicesAdminClient;

/**
 * TODO: add documentation to this class
 *
 * @author dzo
 * 
 * @param <R> 
 * @param <T> 
 * @param <I> 
 */
public class ElasticSearchIndexContainer<R extends Resource> extends SearchIndexContainer<R, Map<String, Object>, ElasticSearchIndex<R>> {
	private ESClient esClient;
	private URI systemURI;
	
	/**
	 * @param resourceClass 
	 * @param id
	 * @param activeIndex
	 * @param inactiveIndex
	 * @param converter
	 * @param esClient 
	 * @param systemURI 
	 */
	public ElasticSearchIndexContainer(Class<R> resourceClass, String id, ResourceConverter<R, Map<String, Object>> converter, final ESClient esClient, final URI systemURI) {
		super(resourceClass, id, converter);
		this.esClient = esClient;
		this.systemURI = systemURI;
		this.initIndices();
	}
	
	private void initIndices() {
		final String activeIndexAlias = ElasticSearchUtils.getLocalAliasForResource(this.resourceClass, this.systemURI, true);
		
		final IndicesAdminClient indicesClient = this.esClient.getClient().admin().indices();
		if (!indicesClient.getAliases(new GetAliasesRequest().aliases(activeIndexAlias)).actionGet().getAliases().isEmpty()) {
			this.activeIndex = new ElasticSearchIndex<>(activeIndexAlias, this, this.resourceClass);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchIndexContainer#createUpdaterForIndex(org.bibsonomy.search.management.SearchIndex)
	 */
	@Override
	public SearchIndexUpdater<R> createUpdaterForIndex(ElasticSearchIndex<R> index) {
		return new ElasticSearchIndexUpdater<R>(this.esClient, index);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchIndexContainer#createRegeneratorTaskForIndex(java.lang.String, org.bibsonomy.search.management.database.SearchDBInterface)
	 */
	@Override
	public SearchIndexGeneratorTask<R, ElasticSearchIndex<R>> createRegeneratorTaskForIndex(String indexId, SearchDBInterface<R> inputLogic) {
		final ElasticSearchIndex<R> oldIndex;
//		// indexId == indexName
//		if (this.activeIndex.getIndexName().equals(indexId)) {
//			// TODO: switch indices, inactive index maybe updating while we try to regenerate the index
//			oldIndex = this.activeIndex;
//		} else {
//			oldIndex = this.inactiveIndex;
//		}
		
		final String newIndexName = ElasticSearchUtils.getIndexNameWithTime(this.systemURI, this.resourceClass);
		final ElasticSearchIndex<R> newIndex = new ElasticSearchIndex<>(newIndexName, this, this.resourceClass);
		
		return new ElasticSearchIndexGeneratorTask<>(inputLogic, newIndex);
	}

}
