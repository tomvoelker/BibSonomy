package org.bibsonomy.search.es.management;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.generator.ElasticSearchIndexGeneratorTask;
import org.bibsonomy.search.es.management.util.ElasticSearchUtils;
import org.bibsonomy.search.es.update.ElasticSearchIndexUpdater;
import org.bibsonomy.search.generator.SearchIndexGeneratorTask;
import org.bibsonomy.search.management.SearchIndexContainer;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.SearchIndexUpdater;
import org.bibsonomy.search.util.MappingBuilder;
import org.bibsonomy.search.util.ResourceConverter;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.hppc.ObjectLookupContainer;
import org.elasticsearch.common.hppc.cursors.ObjectCursor;

/**
 * TODO: add documentation to this class
 *
 * @author dzo
 * 
 * @param <R> 
 * @param <T> 
 * @param <I> 
 */
public class ElasticSearchIndexContainer<R extends Resource> extends SearchIndexContainer<R, Map<String, Object>, ElasticSearchIndex<R>, String> {
	private static final Log log = LogFactory.getLog(ElasticSearchIndexContainer.class);
	
	private ESClient esClient;
	private URI systemURI;
	
	/**
	 * @param resourceClass 
	 * @param id
	 * @param activeIndex
	 * @param inactiveIndex
	 * @param converter
	 * @param mappingBuilder 
	 * @param esClient 
	 * @param systemURI 
	 */
	public ElasticSearchIndexContainer(Class<R> resourceClass, String id, ResourceConverter<R, Map<String, Object>> converter, final MappingBuilder<String> mappingBuilder, final ESClient esClient, final URI systemURI) {
		super(resourceClass, id, converter, mappingBuilder);
		this.esClient = esClient;
		this.systemURI = systemURI;
		this.initIndices();
	}
	
	private void initIndices() {
		/*
		 * look for indices which have the active / unactive aliases set
		 */
		final String activeIndexAlias = ElasticSearchUtils.getLocalAliasForResource(this.getResourceType(), this.systemURI, true);
		
		final String activeIndexName = this.esClient.getIndexNameForAlias(activeIndexAlias);
		if (present(activeIndexName)) {
			this.activeIndex = new ElasticSearchIndex<>(activeIndexName, this);
		}
		
		final String inactiveIndexAlias = ElasticSearchUtils.getLocalAliasForResource(this.getResourceType(), this.systemURI, false);
		final String inactiveIndexName = this.esClient.getIndexNameForAlias(inactiveIndexAlias);
		
		if (present(inactiveIndexName)) {
			this.inactiveIndex = new ElasticSearchIndex<>(inactiveIndexName, this);
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
	 * @see org.bibsonomy.search.management.SearchIndexContainer#replaceOldIndexWithNewOne(org.bibsonomy.search.management.SearchIndex, org.bibsonomy.search.management.SearchIndex)
	 */
	@Override
	public void replaceOldIndexWithNewOne(ElasticSearchIndex<R> oldIndex, ElasticSearchIndex<R> newIndex) {
		this.activateIndex(newIndex);
		
		if (oldIndex != null) {
			final String oldIndexName = oldIndex.getIndexName();
			final ClusterStateRequest clusterStateRequest = new ClusterStateRequest().indices(oldIndexName);
			// TODO: move to es client
			final ObjectLookupContainer<String> aliases = this.esClient.getClient().admin().cluster().state(clusterStateRequest).actionGet().getState().getMetaData().aliases().keys();
			if (!aliases.isEmpty()) {
				throw new IllegalStateException("Found aliases for index '" + oldIndexName + "' while trying to delete index.");
			}
			
			final boolean deleted = this.esClient.deleteIndex(oldIndexName);
			if (deleted) {
				log.debug("deleted index '" + oldIndexName + "'.");
				this.deletedIndex(oldIndex);
			} else {
				log.error("can't delete index '" + oldIndexName + "'.");
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchIndexContainer#doSwitchIndex(org.bibsonomy.search.management.SearchIndex, org.bibsonomy.search.management.SearchIndex, org.bibsonomy.search.management.SearchIndex)
	 */
	@Override
	protected void doSwitchIndex(ElasticSearchIndex<R> oldActiveIndex, ElasticSearchIndex<R> newActiveIndex, ElasticSearchIndex<R> inactiveIndex) {
		final String activeIndexAliasName = ElasticSearchUtils.getLocalAliasForResource(this.getResourceType(), this.systemURI, true);
		final String inactiveAliasName = ElasticSearchUtils.getLocalAliasForResource(this.getResourceType(), this.systemURI, false);
		final IndicesAliasesRequestBuilder prepareAliases = this.esClient.getClient().admin().indices().prepareAliases();
		if (oldActiveIndex != null) {
			prepareAliases.removeAlias(oldActiveIndex.getIndexName(), activeIndexAliasName)
							.addAlias(oldActiveIndex.getIndexName(), inactiveAliasName);
		}
		
		if (inactiveIndex != null) {
			prepareAliases.removeAlias(inactiveIndex.getIndexName(), inactiveAliasName);
		}
		
		prepareAliases.addAlias(newActiveIndex.getIndexName(), activeIndexAliasName);
		final IndicesAliasesResponse aliasReponse = prepareAliases.execute().actionGet();
		if (!aliasReponse.isAcknowledged()) {
			log.error("error switching indices.");
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchIndexContainer#createRegeneratorTaskForIndex(java.lang.String, org.bibsonomy.search.management.database.SearchDBInterface)
	 */
	@Override
	public SearchIndexGeneratorTask<R, ElasticSearchIndex<R>> createRegeneratorTaskForIndex(String indexId, SearchDBInterface<R> inputLogic) {
		// TODO: lock the index
		final ElasticSearchIndex<R> oldIndex = this.inactiveIndex;
		this.inactiveIndex = null;
		
		final String newIndexName = ElasticSearchUtils.getIndexNameWithTime(this.systemURI, this.getResourceType());
		final ElasticSearchIndex<R> newIndex = new ElasticSearchIndex<>(newIndexName, this);
		
		return new ElasticSearchIndexGeneratorTask<>(inputLogic, newIndex, oldIndex);
	}
	
	private String getThisSystemsIndexNameFromAlias(String aliasName) {
		List<String> indexList = getThisSystemsIndexesFromAlias(aliasName);
		if (indexList.size() > 1) {
			log.warn("local system has more than one index for " + aliasName + ": " + indexList);
		}
		if (indexList.size() < 1) {
			log.warn("local system has no index for " + aliasName);
			return null;
		}
		return indexList.get(0);
	}
	
	/**
	 * gets all the indexes set under the alias for the current system
	 *  
	 * @param alias
	 * @return return a list of indexes
	 */
	public List<String> getThisSystemsIndexesFromAlias(String alias) {
		final String thisSystemPrefix = ElasticSearchUtils.normSystemHome(this.systemURI);
		final List<String> rVal = getIndexesFromAlias(alias);
		for (final Iterator<String> it = rVal.iterator(); it.hasNext();) {
			final String indexName = it.next();
			if (!indexName.contains(thisSystemPrefix)) {
				it.remove();
			}
		}
		return rVal;
	}
	
	private List<String> getIndexesFromAlias(String alias) {
		final List<String> indexes = new ArrayList<String>();
		final ImmutableOpenMap<String, AliasMetaData> indexToAliasesMap = this.esClient.getClient().admin().cluster() //
				.state(Requests.clusterStateRequest()) //
				.actionGet() //
				.getState() //
				.getMetaData() //
				.aliases().get(alias);
		if (indexToAliasesMap != null && !indexToAliasesMap.isEmpty()) {
			for (final ObjectCursor<String> cursor : indexToAliasesMap.keys()) {
				indexes.add(cursor.value);
			}
		}
		return indexes;
	}
}
