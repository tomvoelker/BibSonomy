package org.bibsonomy.es;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.es.ESClient;
import org.bibsonomy.model.es.IndexUpdater;
import org.bibsonomy.model.es.UpdatePlugin;

/**
 * Initiates the IndexUpdater for the kronjobs to update indexes
 * 
 * @author lutful
 * @param <R> 
 */
public class SharedIndexUpdatePlugin<R extends Resource> implements UpdatePlugin {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.es.UpdatePlugin#createUpdater(java.lang.String)
	 */
	@Override
	public IndexUpdater createUpdater(String indexType, ESClient esClient) {
		SharedResourceIndexUpdater<R> sharedIndexUpdater;
		sharedIndexUpdater = new SharedResourceIndexUpdater<R>();
		sharedIndexUpdater.setEsClient(esClient);
		sharedIndexUpdater.setIndexType(indexType);
		return sharedIndexUpdater;
	}

}
