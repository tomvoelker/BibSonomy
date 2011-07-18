package org.bibsonomy.database.managers.chain;


/**
 * This represents one element for the chain of responsibility. Classes that
 * bundle a lot of elements for a chain can implement this interface so that
 * they're able to hand out the first element of their chain.
 * 
 * @param <L> Type of the fetched result entities
 * @param <P> Type of the param object
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public interface FirstChainElement<L, P> {

	/**
	 * @return the first element from the chain.
	 */
	public ChainElement<L, P> getFirstElement();
}