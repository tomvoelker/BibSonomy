package org.bibsonomy.database.managers.chain.goldstandard;

import java.util.Map;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.managers.GoldStandardDatabaseManager;
import org.bibsonomy.database.managers.chain.ListChainElement;
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
public abstract class GoldStandardChainElement<RR extends Resource, R extends Resource & GoldStandard<RR>, P extends ResourceParam<RR>> extends ListChainElement<Post<R>, P> {
	
	private Map<Class<?>, GoldStandardDatabaseManager<?, ?, ?>> dbs;
	
	@SuppressWarnings("unchecked")
	protected GoldStandardDatabaseManager<RR, R, P> getDatabaseManagerForType(final Class<?> clazz) {
		if (dbs.containsKey(clazz)) {
			return (GoldStandardDatabaseManager<RR, R, P>) this.dbs.get(clazz);
		}
		
		throw new UnsupportedResourceTypeException(clazz.getName() + " not supported");
	}

	/**
	 * @param dbs the dbs to set
	 */
	public void setDbs(final Map<Class<?>, GoldStandardDatabaseManager<?, ?, ?>> dbs) {
		this.dbs = dbs;
	}
}
