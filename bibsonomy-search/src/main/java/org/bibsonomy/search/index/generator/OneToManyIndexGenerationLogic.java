package org.bibsonomy.search.index.generator;

import java.util.List;

/**
 * interface for a generator logic that saves two types in one index (e.g. a one-to-many relation)
 *
 * @param <T>
 * @param <S>
 */
public interface OneToManyIndexGenerationLogic<T, S> extends IndexGenerationLogic<T> {

	/**
	 * gets the list of the to many objects
	 * @param lastContentId
	 * @param limit
	 * @return
	 */
	List<S> getToManyEntities(final int lastContentId, final int limit);

	/**
	 * returns the number of to many entites in the database
	 * @return
	 */
	int getNumberOfToManyEntities();
}
