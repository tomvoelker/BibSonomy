package org.bibsonomy.model.es;


/**
 * TODO: add documentation to this class
 *
 * @author lutful
 */
public interface UpdatePlugin {
	
	/**
	 * @param indexType
	 * @return the IndexUpdater
	 */
	IndexUpdater createUpdater(String indexType);
}
