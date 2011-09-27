package org.bibsonomy.database.managers.chain.resource;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.PostDatabaseManager;
import org.bibsonomy.database.managers.chain.ListChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
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
public abstract class ResourceChainElement<R extends Resource, P extends ResourceParam<R>> extends ListChainElement<Post<R>, P> {

	// TODO: config via spring
	private static final Map<Class<?>, PostDatabaseManager<?, ?>> dbs;
	
	static {
		dbs = new HashMap<Class<?>, PostDatabaseManager<?, ?>>();
		
		dbs.put(Bookmark.class, BookmarkDatabaseManager.getInstance());
		dbs.put(BibTex.class, BibTexDatabaseManager.getInstance());
	}
	
	@SuppressWarnings("unchecked")
	protected PostDatabaseManager<R, P> getDatabaseManagerForType(final Class<?> clazz) {
		if (dbs.containsKey(clazz)) {
			return (PostDatabaseManager<R, P>) dbs.get(clazz);
		}
		
		throw new UnsupportedResourceTypeException("resource " + clazz.getName() + " not supported");
	}
}
