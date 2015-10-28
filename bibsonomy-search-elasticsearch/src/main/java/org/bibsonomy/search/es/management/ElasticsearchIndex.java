package org.bibsonomy.search.es.management;

import org.bibsonomy.model.Resource;

/**
 * class representing a ElasticSearch index
 *
 * @author dzo
 * @param <R> 
 */
public class ElasticsearchIndex<R extends Resource> {
	
	private final String indexName;
	
	/**
	 * @param indexName
	 */
	public ElasticsearchIndex(final String indexName) {
		this.indexName = indexName;
	}

	/**
	 * @return the indexName
	 */
	public String getIndexName() {
		return this.indexName;
	}
}
