package org.bibsonomy.database.managers.chain;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.util.Transaction;

/**
 * Represents one element in the chain of responsibility.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public abstract class ChainElement<L, P extends GenericParam> implements ChainPerform<P, List<L>, L> {

	/** Logger */
	protected static final Logger log = Logger.getLogger(ChainElement.class);
	protected final GeneralDatabaseManager generalDb;
	/** The next element of the chain */
	private ChainElement<L, P> next;

	public ChainElement() {
		this.generalDb = GeneralDatabaseManager.getInstance();
	}

	/**
	 * Sets the next element in the chain.
	 */
	public final void setNext(final ChainElement<L, P> nextElement) {
		this.next = nextElement;
	}

	public final List<L> perform(final P param, final Transaction session) {
		if (this.canHandle(param)) {
			return this.handle(param, session);
		} else {
			if (this.next != null) {
				return this.next.perform(param, session);
			}
		}
		throw new RuntimeException("Can't handle request.");
	}

	/**
	 * Handles the request.
	 */
	protected abstract List<L> handle(P param, Transaction session);

	/**
	 * Returns true if the request can be handled by the current chain element, otherwise false.
	 */
	protected abstract boolean canHandle(P param);
}