package org.bibsonomy.database.managers.chain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.managers.GroupDatabaseManager;

/**
 * Represents one element in the chain of responsibility.
 * 
 * @param <L> Type of the fetched result entities
 * @param <P> Type of the param object
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public abstract class ChainElement<L, P> {
	protected static final Log log = LogFactory.getLog(ChainElement.class);
	
	protected final GeneralDatabaseManager generalDb;
	protected final GroupDatabaseManager groupDb;

	/**
	 * Constructor
	 */
	public ChainElement() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.groupDb = GroupDatabaseManager.getInstance();
	}

	/**
	 * Handles the request.
	 */
	protected abstract L handle(P param, DBSession session);

	/**
	 * Returns true if the request can be handled by the current chain element,
	 * otherwise false.
	 */
	protected abstract boolean canHandle(P param);
}