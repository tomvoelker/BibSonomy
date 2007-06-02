package org.bibsonomy.database.managers.chain;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.util.Transaction;

/**
 * Represents one element in the chain of responsibility.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
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
		this.next = null;
	}

	/**
	 * Sets the next element in the chain.
	 */
	public final void setNext(final ChainElement<L, P> nextElement) {
		this.next = nextElement;
	}

	public final List<L> perform(final P param, final Transaction session) {
		if (this.canHandle(param)) {
			log.debug(this.getClass().getSimpleName());
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

	protected boolean present(final String s) {
		return ((s != null) && (s.length() > 0));
	}

	protected boolean present(final Collection c) {
		return ((c != null) && (c.size() > 0));
	}

	protected boolean present(final Object o) {
		return (o != null);
	}

	protected boolean present(final GroupID gid) {
		return ((gid != null) && (gid != GroupID.GROUP_INVALID));
	}

	protected boolean presentValidGroupId(final int gid) {
		return (gid != GroupID.GROUP_INVALID.getId());
	}

	protected boolean nullOrEqual(final Object requested, final Object supported) {
		return ((requested == null) || (requested == supported));
	}
}