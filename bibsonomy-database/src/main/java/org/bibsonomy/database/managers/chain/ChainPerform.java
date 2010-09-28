package org.bibsonomy.database.managers.chain;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.GenericParam;

/**
 * This interface encapsulates the getter for a L object
 *
 * @param <L> Type of the fetched result entities
 * @param <P> Type of the param object
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public interface ChainPerform<P extends GenericParam, L> {

	/**
	 * Walks through the chain until a ChainElement is found that can handle the
	 * request.
	 * 
	 * @param param describes the requirements of the request
	 * @param session a database session
	 * @return the list of entities, which is returned by the fitting chainelement 
	 */
	public L perform(P param, DBSession session);
}