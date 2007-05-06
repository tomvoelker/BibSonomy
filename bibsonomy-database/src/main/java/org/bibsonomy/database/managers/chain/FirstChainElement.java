package org.bibsonomy.database.managers.chain;

import org.bibsonomy.model.Resource;

/**
 * This represents one element for the chain of responsibility. Classes that
 * bundle a lot of elements for a chain can implement this interface so that
 * they're able to hand out the first element of their chain.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public interface FirstChainElement<T extends Resource> {

	/**
	 * Returns the first element from the chain.
	 */
	public ChainElement<T> getFirstElement();
}