package org.bibsonomy.search.model;

/**
 * enum representing the different states of an index
 * 
 * @author dzo
 */
public enum SearchIndexState {
	/** the index is currently used for search */
	ACTIVE,
	
	/** the index can be used for updating */
	INACTIVE,
	
	/** the index is currently generating */
	GENERATING;
}
