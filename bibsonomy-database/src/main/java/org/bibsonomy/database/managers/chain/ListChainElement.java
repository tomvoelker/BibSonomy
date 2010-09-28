package org.bibsonomy.database.managers.chain;

import java.util.List;

import org.bibsonomy.database.params.GenericParam;

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
public abstract class ListChainElement<L, P extends GenericParam> extends ChainElement<List<L>, P> {
	
}