package org.bibsonomy.database.managers.chain;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.util.ValidationUtils;

/**
 * Represents one element in the chain of responsibility.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public abstract class ChainElement<L, P extends GenericParam> implements ChainPerform<P, List<L>, L> {

	protected static final Logger log = Logger.getLogger(ChainElement.class);
	protected final GeneralDatabaseManager generalDb;
	private final ValidationUtils check;
	/** The next element of the chain */
	private ChainElement<L, P> next;

	public ChainElement() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.check = ValidationUtils.getInstance();
		this.next = null;
	}

	/**
	 * Sets the next element in the chain.
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
	 * Returns true if the request can be handled by the current chain element, otherwise false.
	 */
	protected abstract boolean canHandle(P param);

	// FIXME: remove me!
	protected boolean present(final String s) {
		return this.check.present(s);
	}

	// FIXME: remove me!
	protected boolean present(final Collection<?> c) {
		return this.check.present(c);
	}

	// FIXME: remove me!
	protected boolean present(final Object o) {
		return this.check.present(o);
	}

	// FIXME: remove me!
	protected boolean present(final GroupID gid) {
		return this.check.present(gid);
	}

	// FIXME: remove me!
	protected boolean presentValidGroupId(final int gid) {
		return this.check.presentValidGroupId(gid);
	}

	// FIXME: remove me!
	protected boolean nullOrEqual(final Object requested, final Object supported) {
		return this.check.nullOrEqual(requested, supported);
	}
}