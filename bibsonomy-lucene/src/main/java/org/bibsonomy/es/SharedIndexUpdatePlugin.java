package org.bibsonomy.es;

import java.util.List;

import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.lucene.util.generator.GenerateIndexCallback;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.es.ESClient;
import org.bibsonomy.model.es.IndexUpdater;
import org.bibsonomy.model.es.SearchType;
import org.bibsonomy.model.es.UpdatePlugin;

/**
 * Initiates the IndexUpdater for the kronjobs to update indexes
 * 
 * @author lutful
 * @param <R> 
 */
public class SharedIndexUpdatePlugin<R extends Resource> implements UpdatePlugin, GenerateIndexCallback<R> {
	private final ESClient esClient;
	private final String systemHome;
	private boolean generatingIndex;
	
	/**
	 * @param esClient
	 * @param systemHome
	 */
	public SharedIndexUpdatePlugin(final ESClient esClient, final String systemHome) {
		this.esClient = esClient;
		this.systemHome = systemHome;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.es.UpdatePlugin#createUpdater(java.lang.String)
	 */
	@Override
	public IndexUpdater createUpdater(String indexType) {
		SharedResourceIndexUpdater<R> sharedIndexUpdater;
		sharedIndexUpdater = new SharedResourceIndexUpdater<R>(this.systemHome);
		sharedIndexUpdater.setEsClient(esClient);
		sharedIndexUpdater.setIndexType(indexType);
		return sharedIndexUpdater;
	}

	/**
	 * generates indexes for shared resource aka ElasticSearch 
	 * @param luceneResourceManagers 
	 */
	@SuppressWarnings("unchecked")
	public void generateIndex(List<LuceneResourceManager<? extends Resource>> luceneResourceManagers) {
		// allow only one index-generation at a time
		synchronized (this) {
			if (this.generatingIndex == true) {
				return;
			}
			this.generatingIndex = true;
		}
		
		SharedResourceIndexGenerator generator = new SharedResourceIndexGenerator(this.systemHome);
		generator.setSearchType(SearchType.ELASTICSEARCH);
		for(LuceneResourceManager<? extends Resource> manager: luceneResourceManagers){
			generator.setLogic((LuceneDBInterface<Resource>) manager.getDbLogic());
			generator.setEsClient(esClient);
			generator.setIndexType(manager.getResourceName());
			generator.setResourceConverter((LuceneResourceConverter<Resource>) manager.getResourceConverter());
			// this cast is really ugly, but safe because nothing specific is done with the resource in the generatedIndex method of this object
			generator.setCallback((GenerateIndexCallback<Resource>) this);
			generator.run();
		}		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.lucene.util.generator.GenerateIndexCallback#generatedIndex(org.bibsonomy.lucene.index.LuceneResourceIndex)
	 */
	@Override
	public void generatedIndex(LuceneResourceIndex<R> index) {
		this.generatingIndex = false;
	}
}
