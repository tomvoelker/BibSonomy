package org.bibsonomy.search.index.generator;

import org.bibsonomy.search.update.SearchIndexSyncState;

import java.util.List;

/**
 * interface for retrieving all necessary information for generating an index
 * @param <T>
 */
public interface IndexGenerationLogic<T> {

	int getNumberOfEntities();

	SearchIndexSyncState getDbState();

	List<T> getEntites(int lastContenId, int limit);
}
