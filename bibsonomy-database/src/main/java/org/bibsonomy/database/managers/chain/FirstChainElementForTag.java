package org.bibsonomy.database.managers.chain;

/**
 * This represents one element for the chain of responsibility. Classes that
 * bundle a lot of elements for a chain can implement this interface so that
 * they're able to hand out the first element of their chain.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public interface FirstChainElementForTag {

	/**
	 * Returns the first element from the chain.
	 */
	public ChainElementForTag getFirstElementForTag();
}