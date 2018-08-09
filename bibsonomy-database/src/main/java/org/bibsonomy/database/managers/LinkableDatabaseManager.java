package org.bibsonomy.database.managers;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.cris.Linkable;

/**
 * common method for a Database manager that is responsible for a {@link org.bibsonomy.model.cris.Linkable} cris entity
 *
 * @author dzo
 */
public interface LinkableDatabaseManager<L extends Linkable> {

	/**
	 * returns the id of the specified linkable
	 * @param linkable
	 * @param session
	 * @return the database id of the entity
	 */
	public Integer getIdForLinkable(final L linkable, final DBSession session);
}
