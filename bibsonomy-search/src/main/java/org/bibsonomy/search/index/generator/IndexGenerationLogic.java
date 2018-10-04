package org.bibsonomy.search.index.generator;

import org.bibsonomy.search.index.database.DatabaseInformationLogic;

import java.util.List;

/**
 * interface for retrieving all necessary information for generating an index
 * @param <T>
 */
public interface IndexGenerationLogic<T> extends DatabaseInformationLogic {

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
	List<T> getEntites(int lastContenId, int limit);
}
