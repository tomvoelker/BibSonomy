package org.bibsonomy.database.managers.chain.goldstandard;

import java.util.List;

import org.bibsonomy.database.managers.GoldStandardDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author dzo
 * @version $Id$
 * @param <RR> 
 * @param <R> 
 * @param <P> 
 */
public abstract class GoldStandardChainElement<RR extends Resource, R extends Resource & GoldStandard<RR>, P extends ResourceParam<RR>> extends ChainElement<List<Post<R>>, P> {
	
	protected GoldStandardDatabaseManager<RR, R, P> databaseManager;

	/**
	 * @param databaseManager the databaseManager to set
	 */
	public void setDatabaseManager(final GoldStandardDatabaseManager<RR, R, P> databaseManager) {
		this.databaseManager = databaseManager;
	}
}
