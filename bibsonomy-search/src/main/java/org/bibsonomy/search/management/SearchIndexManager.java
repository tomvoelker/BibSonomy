package org.bibsonomy.search.management;

import java.util.List;

import org.bibsonomy.model.Resource;
import org.bibsonomy.search.exceptions.IndexAlreadyGeneratingException;
import org.bibsonomy.search.model.SearchIndexInfo;

/**
 * general interface for all search index manager
 * 
 * @author dzo
 * @param <R> 
 */
public interface SearchIndexManager<R extends Resource> {
	
	/**
	 * delete the specified index
	 * @param indexName
	 */
	public void deleteIndex(final String indexName);
	
	/**
	 * update the index
	 */
	public void updateIndex();
	
	/**
	 * @return index information about all managed search indices
	 */
	public List<SearchIndexInfo> getIndexInformations();
	
	/**
	 * regenerate the specified index
	 * @param indexName
	 * @throws IndexAlreadyGeneratingException
	 */
	public void regenerateIndex(final String indexName) throws IndexAlreadyGeneratingException;
	
	/**
	 * regenerate all indices
	 */
	public void regenerateAllIndices();
	
	/**
	 * 
	 * @throws IndexAlreadyGeneratingException
	 */
	public void generateIndex() throws IndexAlreadyGeneratingException;

}
