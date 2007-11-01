package org.bibsonomy.database.managers.chain;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.util.DBSession;

/**
 * Represents one element in the chain of responsibility.
 * @param <L> Type of the fetched result entities
 * @param <P> Type of the param object
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public abstract class ChainElement<L, P extends GenericParam> implements ChainPerform<P, List<L>, L> {

	protected static final Logger log = Logger.getLogger(ChainElement.class);
	protected final GeneralDatabaseManager generalDb;
	/** The next element of the chain */
	private ChainElement<L, P> next;

	/**
	 * abstract base constructs for a chain element (surprise, surprise)
	 */
	public ChainElement() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.next = null;
	}

	/**
	 * Sets the next element in the chain.
	 * @param nextElement the next element following this element
	 */
	public final void setNext(final ChainElement<L, P> nextElement) {
		this.next = nextElement;
	}

	public final List<L> perform(final P param, final DBSession session) {
		if (this.canHandle(param)) {
			log.debug(this.getClass().getSimpleName());
			return this.handle(param, session);
		}
		if (this.next != null) return this.next.perform(param, session);
		throw new RuntimeException("Can't handle request.");
	}

	/**
	 * Handles the request.
	 */
	protected abstract List<L> handle(P param, DBSession session);

	/**
	 * Returns true if the request can be handled by the current chain element,
	 * otherwise false.
	 */
	protected abstract boolean canHandle(P param);
}