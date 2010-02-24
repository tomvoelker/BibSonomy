package org.bibsonomy.lucene.param;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.bibsonomy.lucene.search.collector.TagCountCollector;

/**
 * Container for a query string and a filter string for lucene 
 * 
 * if we can use more than one filter, we should use an arrayList or something
 * like this for filter. with methods addFilter, setFilter, getFilter
 * 
 * @version $Id$
 *
 */
public class QuerySortContainer {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5889003240930421319L;
	
	
	/**
	 * query
	 */
	private Query query;

	/**
	 * filter
	 */
	private Sort sort;
	
	/**
	 * TopDocs collector
	 */
	private TagCountCollector topDocsCollector;

	/**
	 * @return the query
	 */
	public Query getQuery() {
		return this.query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(Query query) {
		this.query = query;
		
	}
	
	/**
	 * @return the query
	 */
	public Sort getSort() {
		return this.sort;
	}

	/**
	 * @param query the query to set
	 */
	public void setSort(Sort sort) {
		this.sort = sort;
		
	}

	public void setTagCountCollector(TagCountCollector topDocsCollector) {
		this.topDocsCollector = topDocsCollector;
	}

	public TagCountCollector getTagCountCollector() {
		return topDocsCollector;
	}
	
}