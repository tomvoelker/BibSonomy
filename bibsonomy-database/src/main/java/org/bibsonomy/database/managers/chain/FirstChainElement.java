package org.bibsonomy.database.managers.chain;

import org.bibsonomy.database.params.GenericParam;

/**
 * This represents one element for the chain of responsibility. Classes that
 * bundle a lot of elements for a chain can implement this interface so that
 * they're able to hand out the first element of their chain.
 * 
 * @param <L> Type of the fetched result entities
 * @param <P> Type of the param object 
 * @author Christian Schenk
 * @version $Id$
 */
public interface FirstChainElement<L, P extends GenericParam> {

	/**
	 * @return the first element from the chain.
	 */
	public ChainElement<L, P> getFirstElement();
}