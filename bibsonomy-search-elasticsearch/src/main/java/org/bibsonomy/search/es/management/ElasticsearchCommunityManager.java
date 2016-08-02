package org.bibsonomy.search.es.management;

import java.util.Date;

import org.bibsonomy.model.Resource;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.management.database.SearchDBInterface;

/**
 * special class that manages community posts
 *
 * @author dzo
 * @param <R> 
 */
public class ElasticsearchCommunityManager<R extends Resource> extends ElasticsearchManager<R> {

	/**
	 * @param updateEnabled
	 * @param client
	 * @param inputLogic
	 * @param tools
	 */
	public ElasticsearchCommunityManager(boolean updateEnabled, ESClient client, SearchDBInterface<R> inputLogic, ElasticsearchIndexTools<R> tools) {
		super(updateEnabled, client, inputLogic, tools);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.management.ElasticsearchManager#updatePredictions(java.lang.String, java.util.Date)
	 */
	@Override
	protected void updatePredictions(String indexName, Date lastLogDate) {
		// nothing to do
	}
}
