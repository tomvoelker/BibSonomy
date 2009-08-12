package org.bibsonomy.database.managers.chain;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DBSessionFactory;
import org.bibsonomy.database.util.IbatisDBSessionFactory;

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
public abstract class ChainElement<L, P extends GenericParam> implements ChainPerform<P, List<L>, L> {

	protected static final Log log = LogFactory.getLog(ChainElement.class);
	protected final GeneralDatabaseManager generalDb;
	protected final GroupDatabaseManager groupDb;
	/** The next element of the chain */
	protected ChainElement<L, P> next;
	
	/** 
	 * This is a quick hack to enable the access of the secondary 
	 * datasource from behind the logic interface
	 */
	protected DBSessionFactory dbSessionFactory;

	/**
	 * Constructor
	 */
	public ChainElement() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.groupDb = GroupDatabaseManager.getInstance();
		this.next = null;
		this.dbSessionFactory = new IbatisDBSessionFactory();
	}

	/**
	 * Sets the next element in the chain.
	 * 
	 * @param nextElement
	 *            the next element following this element
	 */
	public final void setNext(final ChainElement<L, P> nextElement) {
		this.next = nextElement;
	}

	public final List<L> perform(final P param, final DBSession session) {
		return this.perform(param, session, null);
	}

	/**
	 * @param param
	 * @param session
	 * @param chainStatus
	 * @return list of L's
	 * @see #perform(GenericParam, DBSession)
	 * @see ChainStatus
	 * 
	 * XXX: This method is only interesting for unit testing the chain, i.e. if
	 * you want to know which element executed its <code>handle</code> method.
	 */
	public final List<L> perform(final P param, final DBSession session, final ChainStatus chainStatus) {
		if (this.canHandle(param)) {
			if (chainStatus != null) chainStatus.setChainElement(this);
			log.debug("Handling Chain element: " + this.getClass().getSimpleName());
			return this.handle(param, session);
		}
		if (this.next != null) return this.next.perform(param, session, chainStatus);
		throw new RuntimeException("Can't handle request for param object: " + param.toStringByReflection());
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