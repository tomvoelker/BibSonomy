package org.bibsonomy.database.managers.chain.resource;

import java.util.List;

import org.bibsonomy.database.managers.PostDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * 
 * @param <R> the resource of the chain element
 * @param <P> the param of the chain element
 * 
 * @author dzo
 * @version $Id$
 */
public abstract class ResourceChainElement<R extends Resource, P extends ResourceParam<R>> extends ChainElement<List<Post<R>>, P> {
	
	protected PostDatabaseManager<R, P> databaseManager;

	/**
	 * @param databaseManager the databaseManager to set
	 */
	public void setDatabaseManager(final PostDatabaseManager<R, P> databaseManager) {
		this.databaseManager = databaseManager;
	}
}
