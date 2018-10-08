package org.bibsonomy.search.index.update;

import org.bibsonomy.search.index.database.DatabaseInformationLogic;

import java.util.Date;
import java.util.List;

/**
 * logic interface to get updates from the database
 * @param <E>
 */
public interface IndexUpdateLogic<E> extends DatabaseInformationLogic {

	/**
	 * returns the updated entities
	 *
	 * @param lastEntityId
	 * @param lastLogDate
	 * @param size
	 * @param offset
	 * @return
	 */
	List<E> getNewerEntities(long lastEntityId, Date lastLogDate, int size, int offset);

	/**
	 * returns deleted entites
	 * @param lastLogDate
	 * @return
	 */
	List<E> getDeletedEntites(Date lastLogDate);
}
