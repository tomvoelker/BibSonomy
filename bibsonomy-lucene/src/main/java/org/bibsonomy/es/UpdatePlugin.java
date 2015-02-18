package org.bibsonomy.es;


/**
 * TODO: add documentation to this class
 *
 * @author lutful
 */
public interface UpdatePlugin {

	/**
	 * @param indexType
	 * @param esClient
	 * @return IndexUpdater
	 */
	IndexUpdater createUpdater(String indexType);
}
