package org.bibsonomy.database.managers.chain.resource;

import java.util.List;

import org.bibsonomy.database.managers.PostDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.query.PostQuery;

/**
 * query based variant of ResourceChainElement
 *
 * @author dzo
 */
public abstract class QueryBasedResourceChainElement<R extends Resource> extends ChainElement<List<Post<R>>, QueryAdapter<PostQuery<R>>> {

	protected PostDatabaseManager<R, ?> databaseManager;

	/**
	 * @param databaseManager the databaseManager to set
	 */
	public void setDatabaseManager(final PostDatabaseManager<R, ?> databaseManager) {
		this.databaseManager = databaseManager;
	}
}
