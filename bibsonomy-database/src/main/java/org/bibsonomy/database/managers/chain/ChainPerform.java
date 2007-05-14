package org.bibsonomy.database.managers.chain;

import java.util.List;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.util.Transaction;

/**
 * This interface encapsulates the getter for a list of T's (bookmarks,
 * publications, tags, etc.).
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public interface ChainPerform<P extends GenericParam, T extends List<L>, L> {

	/**
	 * Walks through the chain until a ChainElement is found that can handle the
	 * request.
	 */
	public T perform(P param, Transaction session);
}