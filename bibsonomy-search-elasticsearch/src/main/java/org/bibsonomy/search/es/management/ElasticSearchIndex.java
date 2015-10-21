package org.bibsonomy.search.es.management;

import java.util.Map;

import org.bibsonomy.model.Resource;
import org.bibsonomy.search.management.SearchIndex;
import org.bibsonomy.search.management.SearchIndexContainer;

/**
 * class representing a ElasticSearch index
 *
 * @author dzo
 * @param <R> 
 */
public class ElasticSearchIndex<R extends Resource> extends SearchIndex<R, Map<String, Object>, ElasticSearchIndex<R>> {
	
	private final String indexName;
	
	/**
	 * @param indexName 
	 * @param container
	 * @param resourceType 
	 */
	public ElasticSearchIndex(final String indexName, SearchIndexContainer<R, Map<String, Object>, ElasticSearchIndex<R>> container) {
		super(container);
		this.indexName = indexName;
	}

	/**
	 * @return the indexName
	 */
	public String getIndexName() {
		return this.indexName;
	}
}
