package org.bibsonomy.search.index.update;

import org.bibsonomy.search.index.database.DatabaseInformationLogic;

import java.util.List;

/**
 * logic interface to get
 * @param <E>
 */
public interface IndexUpdateLogic<E> extends DatabaseInformationLogic {

	/**
	 * returns the updated persons
	 *
	 * @param lastEntityId
	 * @param size
	 * @param offset
	 * @return
	 */
	List<E> getNewerEntities(long lastEntityId, int size, int offset);
}
