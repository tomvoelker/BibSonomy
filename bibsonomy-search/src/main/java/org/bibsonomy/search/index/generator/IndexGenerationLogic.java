package org.bibsonomy.search.index.generator;

import java.util.List;

/**
 * interface for retrieving all necessary information for generating an index
 * @param <T>
 *
 * @author dzo
 */
public interface IndexGenerationLogic<T> {

	/**
	 * @return the number of entities to insert into the index
	 * (used for progress)
	 */
	int getNumberOfEntities();

	/**
	 * retrieve entities
	 * @param lastContenId
	 * @param limit
	 * @return limit entities starting with last contetn id creater than the provided
	 */
	List<T> getEntities(int lastContenId, int limit);
}
