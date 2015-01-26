package org.bibsonomy.es;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.es.IndexUpdater;
import org.bibsonomy.model.es.UpdatePlugin;

/**
 * Initiates the IndexUpdater for the kronjobs to update indexes
 * 
 * @author lutful
 */
public class SharedIndexUpdatePlugin<R extends Resource> implements UpdatePlugin {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.model.es.UpdatePlugin#createUpdater(java.lang.String)
	 */
	@Override
	public IndexUpdater createUpdater(String indexType) {
		SharedResourceIndexUpdater<R> sharedIndexUpdater;
		sharedIndexUpdater = new SharedResourceIndexUpdater<R>();
		sharedIndexUpdater.setIndexType(indexType);
		return sharedIndexUpdater;
	}

}
